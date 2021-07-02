package com.dmdmax.goonj.screens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dmdmax.goonj.base.BaseFragment
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.screens.views.SettingsView
import com.dmdmax.goonj.screens.views.SubscriptionStatusView

class SettingsFragment: BaseFragment() {

    private lateinit var mView: SettingsView;

    companion object {
        fun newInstance(args: Bundle?): SettingsFragment {
            val fragment = SettingsFragment();
            if (args != null) {
                fragment.arguments = args;
            }
            return fragment;
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = getCompositionRoot().getViewFactory().getBottomSettingsView(container!!);
        return mView.getRootView();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView.initialize();
        EventManager.getInstance(context!!).fireEvent(EventManager.Events.BOTTOM_MENU_MORE_VIEW);
    }

    override fun onResume() {
        super.onResume()
        mView.setUsername();
    }

}