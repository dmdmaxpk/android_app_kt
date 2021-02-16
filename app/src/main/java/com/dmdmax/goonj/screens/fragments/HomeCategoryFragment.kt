package com.dmdmax.goonj.screens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dmdmax.goonj.base.BaseFragment
import com.dmdmax.goonj.screens.views.HomeView

class HomeCategoryFragment: BaseFragment() {

    companion object {
        val ARGS_TAB: String = "args"

        fun newInstance(args: Bundle?): HomeCategoryFragment {
            val fragment: HomeCategoryFragment = HomeCategoryFragment();
            if (args != null) {
                fragment.arguments = args;
            }
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return null;
    }
}