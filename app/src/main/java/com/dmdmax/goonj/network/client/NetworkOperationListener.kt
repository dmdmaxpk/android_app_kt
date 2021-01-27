package com.dmdmax.goonj.network.client

interface NetworkOperationListener {
    fun onSuccess(response: String?)
    fun onFailed(code: Int, reason: String?)
}