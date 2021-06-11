package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView

interface WelcomeView: ObservableView<WelcomeView.Listener> {

    interface Listener {
        fun onSearchClick();
        fun onUserClick();
        fun onBottomClick(position: Int)
    }

    fun  initialize();
    fun bindBottomAdapter();
    fun currentBottomIndex(): Int;
    fun setCurrentBottomIndex(position: Int);
}