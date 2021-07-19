package com.dmdmax.goonj.screens.implements

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.dmdmax.goonj.R
import com.dmdmax.goonj.adapters.GetStartedViewPagerAdapter
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.screens.views.GetStartedView
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class GetStartedImpl: BaseObservableView<GetStartedView.Listener>, GetStartedView, View.OnClickListener {

    private val GET_STARTED_COUNT: Int = 3;
    private lateinit var mAdapter: GetStartedViewPagerAdapter
    private lateinit var mViewPager: ViewPager2
    private lateinit var mIndicator: WormDotsIndicator;

    private lateinit var mSkip: Button;
    private lateinit var mJoin: Button;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.activity_get_started, parent, false));
    }

    override fun initialize() {
        mViewPager = findViewById(R.id.pager);
        mSkip = findViewById(R.id.skip);
        mSkip.setOnClickListener(this);

        mJoin = findViewById(R.id.join);
        mJoin.setOnClickListener(this);

        mIndicator = findViewById(R.id.indicator);
        mIndicator.setDotIndicatorColor(Color.WHITE)
    }

    override fun bindAdapter() {
        mAdapter = GetStartedViewPagerAdapter(getContext() as FragmentActivity, GET_STARTED_COUNT);
        mViewPager.adapter = mAdapter;
        mIndicator.setViewPager2(mViewPager);
        mViewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                if(state == 0){
                    // page changed
                    EventManager.getInstance(getContext()).fireEvent(EventManager.Events.GET_STARTED_SCREEN_SWIPED);
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v){
            mJoin -> {
                for (listener in getListeners()) {
                    listener.next();
                }
            }

            mSkip -> {
                for (listener in getListeners()) {
                    listener.skip();
                }
            }
        }
    }

}
