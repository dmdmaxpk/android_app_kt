package com.dmdmax.goonj.screens.implements

import android.os.Handler
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
import com.dmdmax.goonj.adapters.HomeSliderAdapter
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.gps.GPSHelper
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.City
import com.dmdmax.goonj.models.SliderModel
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.screens.dialogs.DialogManager
import com.dmdmax.goonj.screens.views.LiveTvView
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.JSONParser
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Utility
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import org.json.JSONArray
import java.sql.Time
import java.util.*


class LiveTvImpl: BaseObservableView<LiveTvView.Listener>, LiveTvView {

    private lateinit var mViewPager: ViewPager;
    private lateinit var mIndicator: WormDotsIndicator;
    private lateinit var mSliderAdapter: HomeSliderAdapter;

    private lateinit var mCity: TextView;
    private lateinit var mTime: TextView;
    private lateinit var mNoCitySelection: TextView;

    private lateinit var mTimer: Timer;

    private lateinit var mCatWiseLiveChannels: LinearLayout;

    private var currentItem: Int = 0;

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
        mTimer = Timer();

        mCatWiseLiveChannels = findViewById(R.id.category_wise_live_channels);
        displaySlider();
    }

    override fun displaySlider() {
        getSliderList();
    }

    private fun bindSliderAdapter(list: ArrayList<SliderModel>) {
        mSliderAdapter = HomeSliderAdapter(getContext(), list);
        mViewPager.setClipToPadding(false);
        mViewPager.pageMargin = 20;
        mViewPager.setOffscreenPageLimit(2);

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
                //getLogger().println("onPageSelected");
            }

            override fun onPageScrollStateChanged(state: Int) {
                //getLogger().println("onPageScrollStateChanged");
            }
        });
        slideSlider(list.size);
        displayChannels();
    }

    override fun cancelTimer(){
        mTimer!!.cancel();
    }

    private fun slideSlider(max: Int){
        mTimer.scheduleAtFixedRate(Slide(max), 0, 3000);
    }

    inner class Slide: TimerTask {
        var max: Int = 0;
        constructor(max: Int){
            this.max = max;
        }

        override fun run() {
            currentItem = currentItem.inc();
            if (currentItem >= max) {
                currentItem = 0;
            }
            getLogger().println("run: "+currentItem);
            mViewPager.setCurrentItem(currentItem, true)
        }
    }

    private fun displayChannels(){
        var rootObj = JSONArray(Constants.CATEGORIES_CHANNEL_JSON);
        RestClient(getContext(), Constants.API_BASE_URL + Constants.Companion.EndPoints.LIVE, RestClient.Companion.Method.GET, null, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                getPrefs().setChannels(JSONParser.getLiveChannels(response));
                for (i in 0 until rootObj.length()) {
                    displaySingleCategoryChannel(rootObj.getJSONObject(i).getString("cat_name"), Utility.getCategoryWiseChannel(response, rootObj.getJSONObject(i).getString("url"), null));
                }
            }

            override fun onFailed(code: Int, reason: String?) {
                TODO("Not yet implemented")
            }
        }).exec();
    }

    private fun displaySingleCategoryChannel(category: String, list: ArrayList<Channel>){
        var singleView = LayoutInflater.from(getContext()).inflate(R.layout.cat_wise_channel_layout, null);
        singleView.findViewById<TextView>(R.id.category_name).text = category.toUpperCase();

        var recyclerView = singleView.findViewById<RecyclerView>(R.id.feed);
        setRecyclerView(recyclerView);
        recyclerView.adapter = ChannelsCarouselListAdapter(list, getContext());

        mCatWiseLiveChannels.addView(singleView);
    }


    private fun setRecyclerView(recyclerView: RecyclerView) {
        val horizontalLayoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = horizontalLayoutManager
    }

    private fun getSliderList() {
        RestClient(
                getContext(),
                Constants.API_BASE_URL + Constants.Companion.EndPoints.BANNER,
                RestClient.Companion.Method.GET,
                null,
                object : NetworkOperationListener {
                    override fun onSuccess(response: String?) {
                        bindSliderAdapter(JSONParser.getSlider(response));
                    }

                    override fun onFailed(code: Int, reason: String?) {
                        getLogger().println("Failed to load: "+reason)
                    }
                }).exec();
    }

    override fun displayPrayerTime() {
        if(getPrefs().getCity().isEmpty()){
            mProgressBar.visibility = View.GONE;
            mNoCitySelection.visibility = View.VISIBLE;
            mNoCitySelection.setOnClickListener(object: View.OnClickListener {
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

            /*val mHelper = GPSHelper(getContext());
            if(!mHelper.isLocationEnabled()){
                getLogger().println("isLocationEnabled - not")
                mHelper.displaySwitchOnSettingsDialog();
            }else{
                getLogger().println("isLocationEnabled - yes")
                mHelper.updateLocation(object : GPSHelper.LocationUpdatedListener {
                    override fun onUpdated(lat: Double, lng: Double, alt: Double) {
                        getLogger().println("isLocationEnabled - onUpdated")
                        getPrefs().setCity(mHelper.getCity()!!);
                        getPrefs().setCoords(lat, lng, alt);

                        mCity.text = mHelper.getCity()!!.split(",")[0];
                        mCity.visibility = View.VISIBLE;
                        fetchTodayNamazTimeAndSet(lat, lng, alt);
                    }
                });
            }*/
        }else{
            mNoCitySelection.visibility = View.GONE;
            mCity.text = getPrefs().getCity();
            mCity.visibility = View.VISIBLE;
            fetchTodayNamazTimeAndSet(getPrefs().getLat(), getPrefs().getLng());
        }

        mCity.setOnClickListener(object: View.OnClickListener {
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