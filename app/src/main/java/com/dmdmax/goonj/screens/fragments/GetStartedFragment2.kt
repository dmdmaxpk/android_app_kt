package com.dmdmax.goonj.screens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseFragment

class GetStartedFragment2: BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_get_started_2, container, false)
    }
}