package com.dmdmax.goonj.screens.fragments

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.base.BaseFragment
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.screens.activities.WelcomeActivity
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.views.BottomMenuLiveTvView
import com.dmdmax.goonj.screens.views.WelcomeView

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

    override fun onStart() {
        super.onStart()
        mView.getLogger().println("BottomMenuLiveTvFragment - onStart")
        mView.registerListener(this)
        mView.initialize();
        EventManager.getInstance(context!!).fireEvent(EventManager.Events.BOTTOM_MENU_LIVE_VIEW);

        (context as WelcomeActivity).setFullScreenListener(object: WelcomeActivity.FullScreenListener{
            override fun onFullScreen(isFull: Boolean) {
                mView.setFullscreen(isFull);
            }
        })
    }

    override fun onStop() {
        super.onStop()
        mView.getLogger().println("BottomMenuLiveTvFragment - onStop")
        mView.unregisterListener(this);
        mView.pauseStreaming();

        (context as WelcomeActivity).setFullScreenListener(null);
    }

    override fun goBack() {
        mView.pauseStreaming();
    }

    override fun goToPaywall() {
        getCompositionRoot().getViewFactory().toPaywallScreen(null, PaywallGoonjFragment.SLUG);
    }



}