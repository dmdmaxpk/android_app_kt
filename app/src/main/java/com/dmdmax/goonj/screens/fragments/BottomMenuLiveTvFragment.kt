package com.dmdmax.goonj.screens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dmdmax.goonj.base.BaseFragment
import com.dmdmax.goonj.screens.views.BottomMenuLiveTvView

class BottomMenuLiveTvFragment: BaseFragment(), BottomMenuLiveTvView.Listener {

    private lateinit var mView: BottomMenuLiveTvView;

    companion object {
        fun newInstance(args: Bundle?): BottomMenuLiveTvFragment {
            val fragment = BottomMenuLiveTvFragment();
            if (args != null) {
                fragment.arguments = args;
            }
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = getCompositionRoot().getViewFactory().getBottomMenuLiveTvView(container!!);
        return mView.getRootView();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView.initialize();
    }

    override fun onStart() {
        super.onStart()
        mView.registerListener(this)
    }

    override fun onStop() {
        super.onStop()
        mView.unregisterListener(this);
    }

    override fun goBack() {
        mView.pauseStreaming();
    }
}