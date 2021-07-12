package com.dmdmax.goonj.screens.implements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.dmdmax.goonj.R
import com.dmdmax.goonj.adapters.ChannelsCarouselListAdapter
import com.dmdmax.goonj.adapters.ComedyBannerCarouselListAdapter
import com.dmdmax.goonj.adapters.HomeSliderAdapter
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.models.*
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.screens.dialogs.DialogManager
import com.dmdmax.goonj.screens.fragments.paywall.PaywallBinjeeFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.views.LiveTvView
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.JSONParser
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Utility
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList


class LiveTvImpl: BaseObservableView<LiveTvView.Listener>, LiveTvView {

    private lateinit var mViewPager: ViewPager;
    private lateinit var mIndicator: WormDotsIndicator;
    private lateinit var mSliderAdapter: HomeSliderAdapter;

    private lateinit var mCity: TextView;
    private lateinit var mTime: TextView;
    private lateinit var mNoCitySelection: TextView;

    private lateinit var mCatWiseLiveChannels: LinearLayout;

    private var currentItem: Int = 0;
    var timer: Timer? = null
    val DELAY_MS: Long = 500 //delay in milliseconds before task is to be executed
    val PERIOD_MS: Long = 3000 // time in milliseconds between successive task executions.

    private lateinit var mProgressBar: ProgressBar;
    private lateinit var mNamezTimeLayout: LinearLayout;
    private lateinit var mLeftLayout: LinearLayout;

    constructor(inflater: LayoutInflater, parent: ViewGroup) {
        setRootView(inflater.inflate(R.layout.fragment_livetv, parent, false));
    }

    override fun initialize() {
        mViewPager = findViewById(R.id.slider)
        mIndicator = findViewById(R.id.slider_indicator)

        mCity = findViewById(R.id.city);
        mNoCitySelection = findViewById(R.id.click_here);
        mProgressBar = findViewById(R.id.progress_bar);
        mNamezTimeLayout = findViewById(R.id.namaz_time_layout);
        mLeftLayout = findViewById(R.id.left_layout);
        mTime = findViewById(R.id.time);

        mCatWiseLiveChannels = findViewById(R.id.category_wise_live_channels);
        displaySlider();
    }

    override fun displaySlider() {
        getSliderList();
    }

    private fun bindSliderAdapter(list: ArrayList<SliderModel>) {
        mSliderAdapter = HomeSliderAdapter(getContext(), list, object: HomeSliderAdapter.OnItemClickListener{
            override fun onClick(mode: SliderModel, position: Int) {
                for (listener in getListeners()) {
                    listener.onSliderClick(mSliderAdapter.getDataSet()[position], position);
                }
            }
        });
        mViewPager.clipToPadding = false;
        mViewPager.pageMargin = 10;
        mViewPager.offscreenPageLimit = 3;

        mViewPager.adapter = mSliderAdapter;
        mIndicator.setViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
            ) {
                //getLogger().println("onPageScrolled");
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {
                //getLogger().println("onPageScrollStateChanged");
            }
        });

        displayChannels();
        slideSlider(list.size);
    }

    override fun cancelTimer() {
        timer?.cancel()
    }

    private fun slideSlider(max: Int){
        /*After setting the adapter use the timer */
        val handler = android.os.Handler();
        val Update = Runnable {
            if (currentItem > max) {
                currentItem = -1
            }
            mViewPager.setCurrentItem(currentItem++, true)
        }

        timer = Timer() // This will create a new Thread

        timer?.schedule(object : TimerTask() {
            // task to be scheduled
            override fun run() {
                handler.post(Update)
            }
        }, DELAY_MS, PERIOD_MS)
    }


    private fun displayChannels(){
        var rootObj = JSONArray(Constants.CATEGORIES_CHANNEL_JSON);
        RestClient(getContext(), Constants.API_BASE_URL + Constants.Companion.EndPoints.LIVE, RestClient.Companion.Method.GET, null, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                getPrefs().setChannels(JSONParser.getLiveChannels(response));
                for (i in 0 until rootObj.length()) {
                    displaySingleCategoryChannel(rootObj.getJSONObject(i).getString("cat_name"), Utility.getCategoryWiseChannel(response, rootObj.getJSONObject(i).getString("url"), null));
                }

                // Display Binjee
                displayBinjee()

            }

            override fun onFailed(code: Int, reason: String?) {
                getLogger().println("Failed " + reason)
            }
        }).exec();
    }

    private fun displaySingleCategoryChannel(category: String, list: ArrayList<Channel>){
        var singleView = LayoutInflater.from(getContext()).inflate(R.layout.cat_wise_channel_layout, null);
        singleView.findViewById<TextView>(R.id.category_name).text = category.toUpperCase();

        var recyclerView = singleView.findViewById<RecyclerView>(R.id.feed);
        setRecyclerView(recyclerView);
        recyclerView.adapter = ChannelsCarouselListAdapter(list, getContext(), object : ChannelsCarouselListAdapter.OnItemClickListener {
            override fun onClick(channel: Channel, position: Int) {
                for (listener in getListeners()) {
                    listener.onChannelClick(channel, PaywallGoonjFragment.SLUG);
                }
            }
        });

        mCatWiseLiveChannels.addView(singleView);
    }

    private fun displayComedy(){
        RestClient(getContext(), Constants.COMEDY_BASE_URL + Constants.Companion.EndPoints.COMEDY_GET_SHOWS, RestClient.Companion.Method.GET, null, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                getLogger().println("RESPONSE: $response");
                val videos = JSONParser.getVideos(response, PaywallComedyFragment.SLUG);

                var singleView = LayoutInflater.from(getContext()).inflate(R.layout.cat_wise_channel_layout, null);
                singleView.findViewById<TextView>(R.id.category_name).text = "comedy".toUpperCase();

                var recyclerView = singleView.findViewById<RecyclerView>(R.id.feed);
                setRecyclerView(recyclerView);
                recyclerView.adapter = ComedyBannerCarouselListAdapter(videos, getContext(), object : ComedyBannerCarouselListAdapter.OnItemClickListener {
                    override fun onClick(video: Video, position: Int) {
                        for (listener in getListeners()) {
                            listener.onComedyClick(video, PaywallComedyFragment.SLUG);
                        }
                    }
                });

                mCatWiseLiveChannels.addView(singleView);
            }

            override fun onFailed(code: Int, reason: String?) {
                Logger.println("LiveTvImpl - displayComedy - onFailed: $reason")
            }
        }).execComedy();
    }

    private fun displayBinjee(){
        val postBody: ArrayList<Params> = arrayListOf(
            Params("channel", "APP"),
            Params("refId", "20170101112222"),
            Params("catid", "101")
        );

        RestClient(getContext(), Constants.BINJEE_CONTENT_API_BASE_URL + Constants.Companion.EndPoints.GET_BINJEE_CATEGORIES, RestClient.Companion.Method.POST, postBody, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                Logger.println("RESPONSE BINJEE - $response");
                val videos = JSONParser.getBinjeeCategories(response);

                var singleView = LayoutInflater.from(getContext()).inflate(R.layout.cat_wise_channel_layout, null);
                singleView.findViewById<TextView>(R.id.category_name).text = "binjee".toUpperCase();

                var recyclerView = singleView.findViewById<RecyclerView>(R.id.feed);
                setRecyclerView(recyclerView);
                recyclerView.adapter = ComedyBannerCarouselListAdapter(videos, getContext(), object : ComedyBannerCarouselListAdapter.OnItemClickListener {
                    override fun onClick(video: Video, position: Int) {
                        for (listener in getListeners()) {
                            listener.onBinjeeClick(video, PaywallBinjeeFragment.SLUG);
                        }
                    }
                });

                mCatWiseLiveChannels.addView(singleView);


                // Display Comedy
                displayComedy();
            }

            override fun onFailed(code: Int, reason: String?) {
                Logger.println("LiveTvImpl - displayBinjee - onFailed: $reason")
            }
        }).exec(PaywallBinjeeFragment.SLUG, null);
    }


    private fun setRecyclerView(recyclerView: RecyclerView) {
        val horizontalLayoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = horizontalLayoutManager
    }

    private fun getSliderList() {
        RestClient(
                getContext(),
                Constants.API_BASE_URL + Constants.Companion.EndPoints.SUBCATEGORY + "drama",
                RestClient.Companion.Method.GET,
                null,
                object : NetworkOperationListener {
                    override fun onSuccess(response: String?) {
                        var slider: ArrayList<SliderModel> = arrayListOf();
                        if(Constants.EXTERNAL_HOME_SLIDER_STRING_JSON.isNotEmpty()){
                            slider.addAll(JSONParser.getSlider(Constants.EXTERNAL_HOME_SLIDER_STRING_JSON, getContext()));
                        }
                        slider.addAll(JSONParser.getSlider(response, getContext()))
                        bindSliderAdapter(slider);
                    }

                    override fun onFailed(code: Int, reason: String?) {
                        getLogger().println("Failed to load: " + reason)
                    }
                }).exec();
    }

    override fun displayPrayerTime() {
        if(getPrefs().getCity().isEmpty()){
            mProgressBar.visibility = View.GONE;
            mNoCitySelection.visibility = View.VISIBLE;
            mNoCitySelection.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    DialogManager().displayCityDialog(getContext(), object : DialogManager.CitySelectionListener {
                        override fun onCitySelected(city: City) {
                            mCity.text = city.getCity();
                            mCity.visibility = View.VISIBLE;
                            getPrefs().setCity(city.getCity());
                            getPrefs().setCoords(city.getLatitude(), city.getLongitude());
                            mNoCitySelection.visibility = View.GONE;
                            fetchTodayNamazTimeAndSet(getPrefs().getLat(), getPrefs().getLng());
                        }
                    });
                }
            });
        }else{
            mNoCitySelection.visibility = View.GONE;
            mCity.text = getPrefs().getCity();
            mCity.visibility = View.VISIBLE;
            fetchTodayNamazTimeAndSet(getPrefs().getLat(), getPrefs().getLng());
        }

        mCity.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                DialogManager().displayCityDialog(getContext(), object : DialogManager.CitySelectionListener {
                    override fun onCitySelected(city: City) {
                        mCity.text = city.getCity();
                        mCity.visibility = View.VISIBLE;
                        mProgressBar.visibility = View.VISIBLE;

                        getPrefs().setCity(city.getCity());
                        getPrefs().setCoords(city.getLatitude(), city.getLongitude());
                        fetchTodayNamazTimeAndSet(getPrefs().getLat(), getPrefs().getLng());
                    }
                });
            }
        });
    }

    private fun fetchTodayNamazTimeAndSet(lat: Double, lng: Double){
        RestClient(
                getContext(), "https://api.pray.zone/v2/times/today.json?latitude=${lat}&longitude=${lng}&elevation=62.75",
                RestClient.Companion.Method.GET,
                null,
                object : NetworkOperationListener {
                    override fun onSuccess(response: String?) {
                        val nextNamaz = Utility.getNextNamazTime(response!!);
                        mTime.text = nextNamaz;
                        mProgressBar.visibility = View.GONE;
                        mNamezTimeLayout.visibility = View.VISIBLE
                        mLeftLayout.visibility = View.VISIBLE
                    }

                    override fun onFailed(code: Int, reason: String?) {
                        Logger.println("onFailed -: " + reason);
                    }
                }).exec();
    }
}