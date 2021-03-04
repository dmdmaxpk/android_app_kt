package com.dmdmax.goonj.screens.implements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseFragment
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.models.TabModel
import com.dmdmax.goonj.screens.fragments.HomeCategoryFragment
import com.dmdmax.goonj.screens.fragments.hometabs.LiveTvFragment
import com.dmdmax.goonj.screens.views.HomeView
import com.dmdmax.goonj.utility.*
import com.google.android.material.tabs.TabLayout
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class HomeViewImpl: BaseObservableView<HomeView.Listener>, HomeView {

    private var mViewPager: CustomViewPager;
    private var mChildFragmentManager: FragmentManager;
    private var mTabsLayout: TabLayout;

    private var mTopBar: FrameLayout? = null
    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig;

    constructor(inflater: LayoutInflater, parent: ViewGroup, childFragmentManager: FragmentManager) {
        setRootView(inflater.inflate(R.layout.fragment_home, parent, false));
        mChildFragmentManager = childFragmentManager
        mViewPager = findViewById(R.id.vp)
        mTabsLayout = findViewById(R.id.tab_layout)
        mTopBar = findViewById(R.id.header_bar);

        mViewPager.isPagingEnabled = false;
    }

    override fun initialize() {
        val mList: ArrayList<BaseFragment> = getChildFragments();
        bindViewPager(mList);
    }

    override fun getRemoteConfigs() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        mFirebaseRemoteConfig.setConfigSettingsAsync(FirebaseRemoteConfigSettings.Builder().build())
        mFirebaseRemoteConfig.fetch(Constants.CONFIG_EXPIRATION_TIME_IN_SEC)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful()) {
                        Utility.setConstants(mFirebaseRemoteConfig)
                        getLogger().println("Fetch successful")
                        mFirebaseRemoteConfig.activate();
                        workingCompleted();
                    }
                }.addOnFailureListener { e ->
                    getLogger().println("Fetch failed: " + e.message)
                    e.printStackTrace()
                    Utility.setConstants(mFirebaseRemoteConfig)
                    workingCompleted();
                }
    }

    private fun workingCompleted() {
        for (listener in getListeners()) {
            listener.onCompleted()
        }
    }


    private fun bindViewPager(categoryFragments: List<BaseFragment>) {
        mTabsLayout.tabMode = TabLayout.MODE_SCROLLABLE
        mTabsLayout.setupWithViewPager(mViewPager);
        mViewPager.adapter = MyPagerAdapter(mChildFragmentManager, categoryFragments)

        mViewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                Logger.println("Page selected")
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun getChildFragments(): ArrayList<BaseFragment>{
        val fragmentList: ArrayList<BaseFragment> = arrayListOf();
        val tabsModelList: ArrayList<TabModel> = arrayListOf();
        tabsModelList.addAll(JSONParser.getTabsList(Constants.CATEGORIES_STRING_JSON))

        for (tab in tabsModelList) {
            val bundle = Bundle()
            bundle.putSerializable(HomeCategoryFragment.ARGS_TAB, tab)
            if(tab.getTabName()!!.toLowerCase().equals("live tv")){
                fragmentList.add(LiveTvFragment.newInstance(bundle))
            }else{
                fragmentList.add(HomeCategoryFragment.newInstance(bundle))
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
            return if (bundle != null && bundle.containsKey(HomeCategoryFragment.ARGS_TAB)) {
                (bundle.getSerializable(HomeCategoryFragment.ARGS_TAB) as TabModel?)!!.getTabName();
            } else ""
        }
    }
}