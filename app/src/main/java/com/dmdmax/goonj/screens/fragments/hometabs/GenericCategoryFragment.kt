package com.dmdmax.goonj.screens.fragments.hometabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dmdmax.goonj.base.BaseFragment
import com.dmdmax.goonj.models.TabModel
import com.dmdmax.goonj.models.Video
import com.dmdmax.goonj.screens.views.GenericCategoryView

class GenericCategoryFragment: BaseFragment(), GenericCategoryView.Listener {

    companion object {
        val ARGS_TAB = "tab";
        fun newInstance(args: Bundle?): GenericCategoryFragment {
            val fragment =  GenericCategoryFragment();
            if (args != null) {
                fragment.arguments = args;
            }
            return fragment;
        }
    }

    private lateinit var mView: GenericCategoryView;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = getCompositionRoot().getViewFactory().getGenericCategoryView(container!!);
        return mView.getRootView();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView.initialize();
        mView.loadVideos(arguments?.get(ARGS_TAB) as TabModel)
    }

    override fun onStart() {
        super.onStart()
        mView.registerListener(this);
    }

    override fun onStop() {
        super.onStop()
        mView.unregisterListener(this);
    }

    override fun onItemClick(video: Video) {
        getCompositionRoot().getViewFactory().toPlayerScreen(video);
    }
}