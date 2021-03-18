package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView

interface SettingsView: ObservableView<SettingsView.Listener> {

    interface Listener {
        fun goBack();
    }

    fun  initialize();
    fun setUsername();
}