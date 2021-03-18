package com.dmdmax.goonj.screens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseFragment
import com.dmdmax.goonj.screens.views.HomeView
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Utility
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class HomeFragment: BaseFragment(), HomeView.Listener {

    private lateinit var mView: HomeView;

    companion object {
        fun newInstance(args: Bundle?): HomeFragment {
            val fragment = HomeFragment();
            if (args != null) {
                fragment.arguments = args;
            }
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = getCompositionRoot().getViewFactory().getHomeView(container!!, childFragmentManager);
        return mView.getRootView();
    }

    override fun onStart() {
        super.onStart()
        mView.getRemoteConfigs();
        mView.registerListener(this)
    }

    override fun onStop() {
        super.onStop()
        mView.unregisterListener(this);
    }

    override fun goBack() {
        TODO("Not yet implemented")
    }

    override fun onCompleted() {
        mView.initialize();
    }
}