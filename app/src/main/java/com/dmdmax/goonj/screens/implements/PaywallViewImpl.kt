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
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.base.BaseFragment
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.Paywall
import com.dmdmax.goonj.payments.ComedyPaymentHelper
import com.dmdmax.goonj.screens.activities.PlayerActivity
import com.dmdmax.goonj.screens.fragments.paywall.PaywallBinjeeFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.views.PaywallView
import com.dmdmax.goonj.utility.*
import com.google.android.material.tabs.TabLayout

class PaywallViewImpl: BaseObservableView<PaywallView.Listener>, PaywallView {

    private lateinit var mViewPager: CustomViewPager;
    private lateinit var mTabsLayout: TabLayout;
    private lateinit var mChildFragmentManager: FragmentManager;
    private var mTopBar: FrameLayout? = null

    private lateinit var mPaywall: String;
    private var mChannel: Channel? = null;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.activity_paywall, parent, false));
    }


    override fun initialize(channel: Channel?, paywall: String) {
        Logger.println("PAYWALL: "+paywall);
        this.mChannel = channel;
        this.mPaywall = paywall;
        mViewPager = findViewById(R.id.vp)
        mTabsLayout = findViewById(R.id.tab_layout)
        mTopBar = findViewById(R.id.header_bar);
        mViewPager.isPagingEnabled = false;

        val mList: ArrayList<BaseFragment> = getChildFragments();
        mChildFragmentManager = (getContext() as BaseActivity).supportFragmentManager;
        bindViewPager(mList);
    }


    private fun bindViewPager(categoryFragments: List<BaseFragment>) {
        mTabsLayout.tabMode = TabLayout.MODE_FIXED
        mTabsLayout.setupWithViewPager(mViewPager);
        mViewPager.adapter = MyPagerAdapter(mChildFragmentManager, categoryFragments)

        if(mPaywall.equals(PaywallGoonjFragment.SLUG)){
            mTabsLayout.getTabAt(1)?.view?.isClickable = false;
            mTabsLayout.getTabAt(2)?.view?.isClickable = false;
            mTabsLayout.selectTab(mTabsLayout.getTabAt(0));
        }else if(mPaywall.equals(PaywallBinjeeFragment.SLUG)){
            mTabsLayout.getTabAt(0)?.view?.isClickable = false;
            mTabsLayout.getTabAt(2)?.view?.isClickable = false;
            mTabsLayout.selectTab(mTabsLayout.getTabAt(1));
        }else{
            mTabsLayout.getTabAt(0)?.view?.isClickable = false;
            mTabsLayout.getTabAt(1)?.view?.isClickable = false;
            mTabsLayout.selectTab(mTabsLayout.getTabAt(2));
        }
    }

    private fun getChildFragments(): ArrayList<BaseFragment> {
        val fragmentList: ArrayList<BaseFragment> = arrayListOf();
        val paywallList: ArrayList<Paywall> = arrayListOf();
        paywallList.addAll(arrayListOf(
            Paywall("0", "Goonj", PaywallGoonjFragment.SLUG),
            Paywall("1", "Binjee", PaywallBinjeeFragment.SLUG),
            Paywall("2", "Comedy", PaywallComedyFragment.SLUG)
        ));

        val bundle = Bundle()
        bundle.putSerializable(PaywallGoonjFragment.ARGS_TAB, paywallList[0])
        fragmentList.add(PaywallGoonjFragment.newInstance(bundle));

        val bundle1 = Bundle()
        bundle1.putSerializable(PaywallBinjeeFragment.ARGS_TAB, paywallList[1])
        fragmentList.add(PaywallBinjeeFragment.newInstance(bundle1));

        val bundle2 = Bundle()
        bundle2.putSerializable(PaywallComedyFragment.ARGS_TAB, paywallList[2])
        fragmentList.add(PaywallComedyFragment.newInstance(bundle2));

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
            return if (bundle != null && bundle.containsKey(PaywallGoonjFragment.ARGS_TAB)) {
                (bundle.getSerializable(PaywallGoonjFragment.ARGS_TAB) as Paywall?)!!.name.toUpperCase();
            } else ""
        }
    }
}