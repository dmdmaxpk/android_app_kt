package com.dmdmax.goonj.network.services

import retrofit2.http.POST

interface VerifyOtpService {
    @POST("/verify")
    fun verifyOtp();
}