package com.dmdmax.goonj.payments

import android.content.Context
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.models.Params
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Toaster
import org.json.JSONObject
import java.util.*

class ComedyPaymentHelper {

    private lateinit var mContext: Context;
    private lateinit var mPrefs: GoonjPrefs;
    private val source = "app";

    constructor(context: Context) {
        this.mContext = context;
        this.mPrefs = GoonjPrefs(context);
    }

    interface VerifyOtpListener{
        fun verifyOtp(verified: Boolean, response: String?, allowedToStream: Boolean);
    }

    fun sendOtp(msisdn: String){
        //{"status":"success","data":"OTP sent to your phone","phone":"03476733767"}
        val postBody: MutableMap<String, String> = HashMap()
        postBody["phone"] = msisdn

        RestClient(mContext, Constants.COMEDY_BASE_URL + Constants.Companion.EndPoints.COMEDY_SEND_COMEDY_OTP, RestClient.Companion.Method.POST, null, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {

                Logger.println("Comedy - sendOtp - onSuccess - $response");

                val rootObj = JSONObject(response)
                val status: String = rootObj.getString("status")
                if (status == "success") {
                    EventManager.getInstance(mContext).fireEvent(EventManager.Events.COMEDY_PAYWALL_OTP_SENT);
                    Toaster.printToast(mContext, "OTP Sent")
                } else {
                    var message = "Failed to send OTP"
                    if (rootObj.has("data")) {
                        message = rootObj.getString("data")
                    }
                    EventManager.getInstance(mContext).fireEvent("${EventManager.Events.COMEDY_PAYWALL_FAILED_TO_SENT_OTP_SENT}${message.replace(" ", "_")}");
                    Toaster.printToast(mContext, message!!)
                }


                mPrefs.setMsisdn(msisdn, PaywallComedyFragment.SLUG);
                mPrefs.setSubscribedPackageId("no-package", PaywallComedyFragment.SLUG);
            }

            override fun onFailed(code: Int, reason: String?) {
                Logger.println("SendOtp - onFailed - $reason");
            }
        }).exec(PaywallComedyFragment.SLUG, postBody);
    }

    fun verifyOtp(msisdn: String?, otp: String?, packageId: String?, listener: VerifyOtpListener?){
        //{"status":"success","message":"Signup successfully",
        // "userData":{"user_id":"14340","name":"","slug":null,"username":"","email":"","password":"0fdb3f0c958aef4253e068907ca833f2","gender":"1","role":"subscriber",
        // "token":null,"theme":"default","theme_color":"#16163F",
        // "join_date":"2020-11-20 12:16:37","last_login":"2020-11-20 12:16:37","deactivate_reason":null,"phone":"3476733767","dob":null,"firebase_auth_uid":null,
        // "status":"1","recharge_date":"2020-11-27","plan_id":"0","is_whitelisted":"0","inactive_date":null,"is_market":"0","tx_id":null,"callback":"0",
        // "callback_id":"0","source_id":null,"trial":"0","active_status":null,"deactivate_date":null}}

        val postBody: MutableMap<String, String> = HashMap()
        postBody["phone"] = msisdn!!
        postBody["code"] = otp!!

        RestClient(mContext, Constants.COMEDY_BASE_URL + Constants.Companion.EndPoints.COMEDY_COMEDY_VERIFY_OTP, RestClient.Companion.Method.POST, null, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                if (response != null) {
                    Logger.println("Comedy - verifyOtp - onSuccess: " + response);
                    val rootObj = JSONObject(response);
                    val status = rootObj.getString("status")

                    if (status == "success") {
                        EventManager.getInstance(mContext).fireEvent(EventManager.Events.COMEDY_PAYWALL_OTP_VERIFIED);
                        Toaster.printToast(mContext, "OTP Validated!")

                        // Validated
                        mPrefs.setOtpValidated(true)
                        mPrefs.setMsisdn(msisdn, PaywallComedyFragment.SLUG)

                        if (rootObj.has("access_token") && rootObj.has("refresh_token")) {
                            mPrefs.setAccessToken(rootObj.getString("access_token"))
                            mPrefs.setRefreshToken(rootObj.getString("refresh_token"))
                        }

                        if (rootObj.has("userData")) {
                            val userData = rootObj.getJSONObject("userData")
                            val subStatus = userData.getInt("status")
                            mPrefs.setUserId(userData.getString("user_id"), PaywallComedyFragment.SLUG)
                            mPrefs.setSubscribedPackageId(userData.getString("plan_id"), PaywallComedyFragment.SLUG)
                            if (subStatus == 1) {
                                // subscribed
                                mPrefs.setStreamable(true, PaywallComedyFragment.SLUG)
                                mPrefs.setSubscriptionStatus("billed", PaywallComedyFragment.SLUG)
                                mPrefs.setMsisdn(msisdn, PaywallComedyFragment.SLUG)
                                EventManager.getInstance(mContext).fireEvent(EventManager.Events.COMEDY_PAYWALL_SUBSCRIBED);
                            } else {
                                // not subscribed
                                mPrefs.setStreamable(false, PaywallComedyFragment.SLUG)
                                mPrefs.setSubscriptionStatus(PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED, PaywallComedyFragment.SLUG)
                                EventManager.getInstance(mContext).fireEvent(EventManager.Events.COMEDY_PAYWALL_FAILED_TO_SUBSCRIBE);
                            }
                            if (subStatus == 1) {
                                // Display stream
                                listener?.verifyOtp(true, response, true)
                            }else{
                                listener?.verifyOtp(false, response, false)
                            }
                        } else {
                            EventManager.getInstance(mContext).fireEvent(EventManager.Events.COMEDY_PAYWALL_FAILED_TO_SUBSCRIBE);
                            Toaster.printToast(mContext, rootObj.getString("data"));
                            listener?.verifyOtp(false, response, false)
                        }
                    }
                    else {
                        EventManager.getInstance(mContext).fireEvent(EventManager.Events.COMEDY_PAYWALL_OTP_NOT_VERIFIED);
                        listener?.verifyOtp(false, response, false)
                    }
                }
            }

            override fun onFailed(code: Int, reason: String?) {
                listener?.verifyOtp(false, null, false)
            }
        }).exec(PaywallComedyFragment.SLUG, postBody);
    }

    interface BillingStatusCheckListener {
        fun onStatus(code: Int, status: String);
    }

    fun checkBillingStatus(msisdn: String, listener: BillingStatusCheckListener?) {
        // {"status":"success","message":"User is not subscribed"}
        val map: MutableMap<String, String> = HashMap()
        map["phone"] = msisdn

        RestClient(mContext, Constants.COMEDY_BASE_URL + Constants.Companion.EndPoints.CHECK_COMEDY_SUBSCRIPTION, RestClient.Companion.Method.POST, null, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                Logger.println("ComedyPaymentHelper - checkBillingStatus: $response");

                try {
                    val rootObj = JSONObject(response)
                    if (rootObj.has("message") && rootObj.getString("message") == "User is subscribed") {
                        mPrefs.setSubscriptionStatus("billed", PaywallComedyFragment.SLUG)
                        mPrefs.setStreamable(true, PaywallComedyFragment.SLUG)
                        mPrefs.setMsisdn(msisdn, PaywallComedyFragment.SLUG)
                        listener?.onStatus(0, PaymentHelper.Companion.PaymentStatus.STATUS_BILLED)
                    } else {
                        mPrefs.setSubscriptionStatus(PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED, PaywallComedyFragment.SLUG)
                        mPrefs.setStreamable(false, PaywallComedyFragment.SLUG)
                        listener?.onStatus(-1, PaymentHelper.Companion.PaymentStatus.STATUS_EXPIRED)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailed(code: Int, reason: String?) {
                Logger.println("Comedy - checkSubscription - onFailed: $reason")
            }
        }).exec(PaywallComedyFragment.SLUG, map);
    }

    interface UnsubscribedListener {
        fun onStatus(code: Int, status: String);
    }
    fun unsubscribe(plaId: String, userId: String, listener: UnsubscribedListener?) {
        val map: MutableMap<String, String> = HashMap()
        map["plan_id"] = plaId
        map["user_id"] = userId

        RestClient(
            mContext,
            Constants.COMEDY_BASE_URL + Constants.Companion.EndPoints.COMEDY_CANCEL_COMEDY_SUBSCRIPTION,
            RestClient.Companion.Method.POST,
            null,
            object : NetworkOperationListener {
                override fun onSuccess(response: String?) {

                    Logger.println("ComedyPaymentHelper - unsubscribe: $response");

                    try {
                        val rootObj = JSONObject(response)
                        if (rootObj.has("value") && rootObj.getString("value") == "already subscriber") {
                            mPrefs.setSubscriptionStatus(PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED, PaywallComedyFragment.SLUG)
                            mPrefs.setStreamable(false, PaywallComedyFragment.SLUG)
                            listener?.onStatus(0, PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED)
                        } else {
                            listener?.onStatus(-1, PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailed(code: Int, reason: String?) {
                    Logger.println("ComedyPaymentHelper - unsubscribe - onFailed: " + reason)
                }
            }).exec(PaywallComedyFragment.SLUG, map);
    }
}