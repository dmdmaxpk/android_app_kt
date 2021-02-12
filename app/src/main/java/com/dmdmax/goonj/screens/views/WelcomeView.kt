package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView

interface WelcomeView: ObservableView<WelcomeView.Listener> {

    interface Listener {
        fun onSearchClick();
        fun onUserClick();
        fun onBottomClick(position: Number)
    }

    fun  initialize();
    fun bindBottomAdapter();
}