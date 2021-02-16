package com.dmdmax.goonj.base

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.dmdmax.goonj.utility.FragmentFrameHelper

class BaseController {

    private lateinit var mActivity: FragmentActivity;

    constructor(activity: FragmentActivity){
        this.mActivity = activity;
    }

    private fun getActivity(): FragmentActivity {
        return mActivity
    }

    private fun getContext(): Context? {
        return mActivity
    }

    private fun getFragmentManager(): FragmentManager {
        return getActivity().supportFragmentManager
    }

    private fun getLayoutInflater(): LayoutInflater {
        return LayoutInflater.from(getContext())
    }

    fun getViewFactory(): BaseViewFactory {
        return BaseViewFactory(getLayoutInflater(), FragmentFrameHelper(getActivity(), getFragmentManager()));
    }
}