package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView

interface GetStartedView: ObservableView<GetStartedView.Listener> {

    interface Listener {
        fun skip()
        fun next()
    }

    fun  initialize();
    fun bindAdapter();
}