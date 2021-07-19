package com.dmdmax.goonj.screens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dmdmax.goonj.base.BaseFragment
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.models.Video
import com.dmdmax.goonj.screens.views.VodView

class VodFragment: BaseFragment(), VodView.Listener {

    private lateinit var mView: VodView;

    companion object {
        fun newInstance(args: Bundle?): VodFragment {
            val fragment = VodFragment();
            if (args != null) {
                fragment.arguments = args;
            }
            return fragment;
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = getCompositionRoot().getViewFactory().getVodImpl(container!!);
        return mView.getRootView();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView.initialize();
        EventManager.getInstance(context!!).fireEvent(EventManager.Events.BOTTOM_MENU_VOD_VIEW);
    }

    override fun onStart() {
        super.onStart();
        mView.registerListener(this);
    }

    override fun onStop() {
        super.onStop();
        mView.unregisterListener(this);
    }

    override fun onBanner(video: Video) {
        com.dmdmax.goonj.utility.Logger.println("onBanner: "+video.getTitle())
        getCompositionRoot().getViewFactory().toPlayerScreen(video);
    }

    override fun onVodClick(video: Video) {
        com.dmdmax.goonj.utility.Logger.println("onVodClick: "+video.getTitle())
        getCompositionRoot().getViewFactory().toPlayerScreen(video);
    }
}