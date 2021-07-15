package com.dmdmax.goonj.network

import androidx.lifecycle.MutableLiveData
const val DISCONNECTED = 0
const val CONNECTED = 1
object NetWorkManger {
    val networkStatus = MutableLiveData<Int>()
    fun isDisconnected(): Boolean {
        return networkStatus.value == DISCONNECTED
    }
}
class NetWorkDisconnectedException : Throwable()