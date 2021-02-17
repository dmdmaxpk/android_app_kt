package com.dmdmax.goonj.screens.implements

import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.get
import androidx.viewpager.widget.ViewPager
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
import com.dmdmax.goonj.utility.SliderAnimation
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class LiveTvImpl: BaseObservableView<LiveTvView.Listener>, LiveTvView {

    private lateinit var mViewPager: ViewPager;
    private lateinit var mIndicator: WormDotsIndicator;
    private lateinit var mSliderAdapter: HomeSliderAdapter;

    private lateinit var mCity: TextView;
    private lateinit var mTime: TextView;

    constructor(inflater: LayoutInflater, parent: ViewGroup) {
        setRootView(inflater.inflate(R.layout.fragment_livetv, parent, false));
    }

    override fun initialize() {
        mViewPager = findViewById(R.id.slider)
        mIndicator = findViewById(R.id.slider_indicator)

        mCity = findViewById(R.id.city);
        mTime = findViewById(R.id.time);

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

        //mViewPager.setPageTransformer(false, SliderAnimation())

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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
        RestClient(getContext(), Constants.API_BASE_URL + Constants.Companion.EndPoints.BANNER, RestClient.Companion.Method.GET, null, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                bindSliderAdapter(JSONParser.getSlider(response));
                Handler().postDelayed({mViewPager.setCurrentItem(1, true)}, 75)
            }

            override fun onFailed(code: Int, reason: String?) {
                TODO("Not yet implemented")
            }
        }).exec();
    }

    override fun displayPrayerTime() {
        val mHelper = GPSHelper(getContext());
        if(!mHelper.isLocationEnabled()){
            mHelper.displaySwitchOnSettingsDialog();
        }else{
            mHelper.updateLocation();
            Handler().postDelayed(Runnable {
                mCity.text = mHelper.getCity();
                fetchTodayNamazTimeAndSet(mHelper.latitude, mHelper.longitude, mHelper.altitude);
            }, 1000)
        }
    }

    private fun fetchTodayNamazTimeAndSet(lat: Double, lng: Double, alt: Double){
        RestClient(getContext(), "https://api.pray.zone/v2/times/today.json?latitude=${lat}&longitude=${lng}&elevation=${alt}", RestClient.Companion.Method.GET, null, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                val nextNamaz = getNextNamazTime(response!!);
                mTime.text = nextNamaz;
            }

            override fun onFailed(code: Int, reason: String?) {
                TODO("Not yet implemented")
            }
        }).exec();
    }

    private fun getNextNamazTime(response: String): String {
        val rootObj = JSONObject(response);

        lateinit var imSak: String;

        if(rootObj.getInt("code") == 200){
            val timesObj: JSONObject = rootObj.getJSONObject("results").getJSONArray("datetime").getJSONObject(0).getJSONObject("times");

            imSak = "Imsak " + timesObj.getString("Imsak");
            val sunrise = "Sunrise " + timesObj.getString("Sunrise");
            val fajr = "Fajr " + timesObj.getString("Fajr");
            val dhuhr = "Dhuhr " + timesObj.getString("Dhuhr");
            val asr = "Asr " + timesObj.getString("Asr");
            val sunset = "Sunset " + timesObj.getString("Sunset");
            val maghrib = "Maghrib " + timesObj.getString("Maghrib");
            val isha = "Isha " + timesObj.getString("Isha");

            val timesArray = arrayOf(imSak, sunrise, fajr, dhuhr, asr, sunset, maghrib, isha);

            for (i in 0..timesArray.size){
                if(hourOf(timesArray[i]) > Calendar.getInstance().timeInMillis){
                    return timesArray[i];
                }
            }
        }

        return imSak;
    }

    private fun hourOf(time: String): Long {
        val mCal: Calendar = Calendar.getInstance();
        mCal.set(Calendar.HOUR_OF_DAY, time.split(' ')[1].split(":")[0].toIntOrNull()!!);
        mCal.set(Calendar.MINUTE, time.split(' ')[1].split(":")[1].toIntOrNull()!!);
        return mCal.timeInMillis;
    }
}