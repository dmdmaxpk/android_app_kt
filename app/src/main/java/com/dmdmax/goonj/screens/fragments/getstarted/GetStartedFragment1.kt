package com.dmdmax.goonj.screens.fragments.getstarted

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseFragment

class GetStartedFragment1: BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_get_started_1, container, false)
    }
}