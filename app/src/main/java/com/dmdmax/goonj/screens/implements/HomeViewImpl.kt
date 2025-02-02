package com.dmdmax.goonj.screens.implements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseFragment
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.models.TabModel
import com.dmdmax.goonj.screens.fragments.hometabs.GenericCategoryFragment
import com.dmdmax.goonj.screens.fragments.hometabs.HomeTabLiveTvFragment
import com.dmdmax.goonj.screens.fragments.hometabs.LiveTvFragment
import com.dmdmax.goonj.screens.views.HomeView
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.CustomViewPager
import com.dmdmax.goonj.utility.JSONParser
import com.dmdmax.goonj.utility.Logger
import com.google.android.material.tabs.TabLayout
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class HomeViewImpl: BaseObservableView<HomeView.Listener>, HomeView {

    private var mViewPager: CustomViewPager;
    private var mChildFragmentManager: FragmentManager;
    private var mTabsLayout: TabLayout;

    private var mTopBar: FrameLayout? = null
    val tabsModelList: ArrayList<TabModel> = arrayListOf();

    constructor(inflater: LayoutInflater, parent: ViewGroup, childFragmentManager: FragmentManager) {
        setRootView(inflater.inflate(R.layout.fragment_home, parent, false));
        mChildFragmentManager = childFragmentManager
        mViewPager = findViewById(R.id.vp)
        mTabsLayout = findViewById(R.id.tab_layout)
        mTopBar = findViewById(R.id.header_bar);

        mViewPager.isPagingEnabled = false;
    }

    override fun initialize() {
        tabsModelList.addAll(JSONParser.getTabsList(Constants.CATEGORIES_STRING_JSON))
        val mList: ArrayList<BaseFragment> = getChildFragments();
        bindViewPager(mList);
    }


    private fun bindViewPager(categoryFragments: List<BaseFragment>) {
        mTabsLayout.tabMode = TabLayout.MODE_SCROLLABLE
        mTabsLayout.setupWithViewPager(mViewPager);
        mViewPager.adapter = MyPagerAdapter(mChildFragmentManager, categoryFragments)

        mViewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                EventManager.getInstance(getContext()).fireEvent(tabsModelList[position].getTabName().toString().replaceAfter(" ", "_"));
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    override fun setOrientation(isFull: Boolean){
        if(isFull){
            mTopBar?.visibility = View.GONE
        }else{
            mTopBar?.visibility = View.VISIBLE;
        }
    }

    private fun getChildFragments(): ArrayList<BaseFragment>{
        val fragmentList: ArrayList<BaseFragment> = arrayListOf();


        for (tab in tabsModelList) {
            val bundle = Bundle()
            bundle.putSerializable(GenericCategoryFragment.ARGS_TAB, tab)

            if(tab.getCategory().equals("live")){
                fragmentList.add(HomeTabLiveTvFragment.newInstance(bundle))
            }else if(tab.getTabName()!!.toLowerCase().equals("live tv")){
                fragmentList.add(LiveTvFragment.newInstance(bundle))
            }else{
                fragmentList.add(GenericCategoryFragment.newInstance(bundle))
            }
        }
        return fragmentList;
    }

    internal class MyPagerAdapter(manager: FragmentManager, private val categoryFragments: List<BaseFragment>) : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return categoryFragments[position]
        }

        override fun getCount(): Int {
            return categoryFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            val bundle = categoryFragments[position].arguments
            return if (bundle != null && bundle.containsKey(GenericCategoryFragment.ARGS_TAB)) {
                (bundle.getSerializable(GenericCategoryFragment.ARGS_TAB) as TabModel?)!!.getTabName();
            } else ""
        }
    }
}