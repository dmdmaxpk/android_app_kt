package com.dmdmax.goonj.screens.implements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.dmdmax.goonj.R
import com.dmdmax.goonj.adapters.HomeSliderAdapter
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.gps.GPSHelper
import com.dmdmax.goonj.models.SliderModel
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.screens.views.LiveTvView
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.JSONParser
import com.dmdmax.goonj.utility.Logger
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import java.lang.Exception
import java.util.logging.Handler
import kotlin.math.ln

class LiveTvImpl: BaseObservableView<LiveTvView.Listener>, LiveTvView {

    private lateinit var mViewPager: ViewPager;
    private lateinit var mIndicator: WormDotsIndicator;
    private lateinit var mSliderAdapter: HomeSliderAdapter;

    constructor(inflater: LayoutInflater, parent: ViewGroup) {
        setRootView(inflater.inflate(R.layout.fragment_livetv, parent, false));
    }

    override fun initialize() {
        mViewPager = findViewById(R.id.slider)
        mIndicator = findViewById(R.id.slider_indicator)

        displaySlider();
    }

    override fun displaySlider() {
        getSliderList();
    }

    private fun bindSliderAdapter(list: ArrayList<SliderModel>) {
        mSliderAdapter = HomeSliderAdapter(getContext(), list);
        mViewPager.setClipToPadding(false);
        mViewPager.pageMargin = 20;

        mViewPager.adapter = mSliderAdapter;
        mIndicator.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                //getLogger().println("onPageScrolled");
            }

            override fun onPageSelected(position: Int) {
                //getLogger().println("onPageSelected");
            }

            override fun onPageScrollStateChanged(state: Int) {
                //getLogger().println("onPageScrollStateChanged");
            }
        })
    }

    private fun getSliderList() {
        RestClient(getContext(), Constants.API_BASE_URL + Constants.Companion.EndPoints.BANNER, RestClient.Companion.Method.GET, null, object: NetworkOperationListener{
            override fun onSuccess(response: String?) {
                bindSliderAdapter(JSONParser.getSlider(response));
                mViewPager.setCurrentItem(1);
            }

            override fun onFailed(code: Int, reason: String?) {
                TODO("Not yet implemented")
            }
        }).exec();
    }

    override fun displayPrayerTime() {
        getLogger().println("displayPrayerTime");
        val mHelper = GPSHelper(getContext());
        if(!mHelper.isLocationEnabled()){
            mHelper.displaySwitchOnSettingsDialog();
        }else{
            mHelper.updateLocation();
            android.os.Handler().postDelayed(Runnable {
                fetchTodayNamazTime(mHelper.latitude, mHelper.longitude, mHelper.altitude);
            }, 2000)
        }
    }

    private fun fetchTodayNamazTime(lat: Double, lng: Double, alt: Double){
        RestClient(getContext(), "https://api.pray.zone/v2/times/today.json?latitude=${lat}&longitude=${lng}&elevation=${alt}", RestClient.Companion.Method.GET, null, object: NetworkOperationListener{
            override fun onSuccess(response: String?) {
                Logger.println(response!!);
            }

            override fun onFailed(code: Int, reason: String?) {
                TODO("Not yet implemented")
            }
        }).exec();
    }
}