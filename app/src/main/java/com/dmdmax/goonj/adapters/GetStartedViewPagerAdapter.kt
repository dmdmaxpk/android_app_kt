package com.dmdmax.goonj.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dmdmax.goonj.screens.fragments.getstarted.GetStartedFragment1
import com.dmdmax.goonj.screens.fragments.getstarted.GetStartedFragment2
import com.dmdmax.goonj.screens.fragments.getstarted.GetStartedFragment3
import com.dmdmax.goonj.utility.Logger

class GetStartedViewPagerAdapter: FragmentStateAdapter {

    private var mCount: Int = 0;

    constructor(fragment: FragmentActivity, count: Int) : super(fragment) {
        this.mCount = count;
    }

    override fun getItemCount(): Int {
        return mCount;
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> {
                Logger.println("GetStartedFragment1");
                return GetStartedFragment1();
            }

            1 -> {
                Logger.println("GetStartedFragment2");
                return GetStartedFragment2();
            }

            2 -> {
                Logger.println("GetStartedFragment3");
                return GetStartedFragment3();
            }

            else -> {
                Logger.println("GetStartedFragment1");
                return GetStartedFragment1();
            }
        }
    }
}