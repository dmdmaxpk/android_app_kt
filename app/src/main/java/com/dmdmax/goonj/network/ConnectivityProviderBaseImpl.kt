package com.dmdmax.goonj.network

import com.dmdmax.goonj.network.NetWorkManger.networkStatus


abstract class ConnectivityProviderBaseImpl : ConnectivityProvider {
    override fun subscribe() {
        subscribeListener()
        //init status
        dispatchChange(getNetworkState())
    }
    protected fun dispatchChange(state: ConnectivityProvider.NetworkState) {
        val networkState = if (state.hasInternet()) CONNECTED else DISCONNECTED
        if (networkState != networkStatus.value) {
            networkStatus.postValue(networkState)
        }
    }
    private fun ConnectivityProvider.NetworkState.hasInternet(): Boolean {
        return (this as? ConnectivityProvider.NetworkState.ConnectedState)?.hasInternet == true
    }
    protected abstract fun subscribeListener()
}