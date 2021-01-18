package com.dmdmax.goonj.screens.views
import com.dmdmax.goonj.base.ObservableView

public interface SplashView: ObservableView<SplashView.Listener> {
    interface Listener {
        fun onCompleted()
    }
    fun getRemoteConfigs();
}