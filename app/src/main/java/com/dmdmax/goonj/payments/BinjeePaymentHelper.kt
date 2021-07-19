package com.dmdmax.goonj.payments

import android.content.Context
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.models.PackageModel
import com.dmdmax.goonj.models.Params
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.screens.fragments.paywall.PaywallBinjeeFragment
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Toaster
import org.json.JSONObject

class BinjeePaymentHelper {

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

        /*{"result":0,"resultCode":"402","pinCode":"","resultString":"success","refId":"2022523463477","value":"already subscriber","data":{}}*/

        val postBody: ArrayList<Params> = arrayListOf(
                Params("msisdn", msisdn),
                Params("password", "null"),
                Params("isSignIn", "0"),
                Params("channel", "APP"),
                Params("txid", "txid"),
                Params("refId", "2022523463477")
        );

        RestClient(mContext, Constants.BINJEE_API_BASE_URL + Constants.Companion.EndPoints.BINJEE_SIGNUP, RestClient.Companion.Method.POST, postBody, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                //{"result":0,"resultCode":"","pinCode":"","resultString":"","refId":"2022523463477","value":"","data":{}}
                Logger.println("Binjee - sendOtp - onSuccess - $response");

                val rootObj = JSONObject(response)
                if(rootObj.getString("value").equals("already subscriber")){
                    // use resend pin api
                    resendOtp(msisdn);
                    return;
                }

                val code = rootObj.getInt("result")
                if (code == 0) {
                    Toaster.printToast(mContext, "OTP Sent")
                    EventManager.getInstance(mContext).fireEvent(EventManager.Events.BINJEE_PAYWALL_OTP_SENT);
                } else {
                    var message: String? = "Failed to send OTP"
                    if (rootObj.has("data")) {
                        message = rootObj.getString("data")
                    }
                    EventManager.getInstance(mContext).fireEvent("${EventManager.Events.BINJEE_PAYWALL_FAILED_TO_SENT_OTP_SENT}${message!!.replace(" ", "_")}");
                    Toaster.printToast(mContext, message!!)
                }


                mPrefs.setMsisdn(msisdn, PaywallBinjeeFragment.SLUG);
                mPrefs.setSubscribedPackageId("no-package", PaywallBinjeeFragment.SLUG);
            }

            override fun onFailed(code: Int, reason: String?) {
                Logger.println("SendOtp - onFailed - $reason");
            }
        }).exec(PaywallBinjeeFragment.SLUG, null);
    }

    fun resendOtp(msisdn: String){

        val postBody: ArrayList<Params> = arrayListOf(
            Params("msisdn", msisdn),
            Params("channel", "APP"),
            Params("refId", "2022523463477")
        );

        RestClient(mContext, Constants.BINJEE_API_BASE_URL + Constants.Companion.EndPoints.BINJEE_SIGNUP_PIN_RESEND, RestClient.Companion.Method.POST, postBody, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                //{"result":0,"resultCode":"","pinCode":"","resultString":"","refId":"2022523463477","value":"","data":{}}
                Logger.println("Binjee - sendOtp - onSuccess - $response");

                val rootObj = JSONObject(response)
                val code = rootObj.getInt("result")
                if (code == 0) {
                    Toaster.printToast(mContext, "OTP Sent")
                    EventManager.getInstance(mContext).fireEvent(EventManager.Events.BINJEE_PAYWALL_OTP_SENT);
                } else {
                    var message: String? = "Failed to send OTP"
                    if (rootObj.has("data")) {
                        message = rootObj.getString("data")
                    }
                    EventManager.getInstance(mContext).fireEvent("${EventManager.Events.BINJEE_PAYWALL_FAILED_TO_SENT_OTP_SENT}${message!!.replace(" ", "_")}");
                    Toaster.printToast(mContext, message!!)
                }


                mPrefs.setMsisdn(msisdn, PaywallBinjeeFragment.SLUG);
                mPrefs.setSubscribedPackageId("no-package", PaywallBinjeeFragment.SLUG);
            }

            override fun onFailed(code: Int, reason: String?) {
                Logger.println("SendOtp - onFailed - $reason");
            }
        }).exec(PaywallBinjeeFragment.SLUG, null);
    }

    fun verifyOtp(msisdn: String?, otp: String?, mPackage: PackageModel, listener: SubscribeNowListener?){

        val postBody: ArrayList<Params> = arrayListOf(
          Params("msisdn", msisdn),
          Params("pinCode",otp),
          Params("channel", "app"),
          Params("refId", "2022523463477")
        );

        RestClient(mContext, Constants.BINJEE_API_BASE_URL + Constants.Companion.EndPoints.BINJEE_VALIDATE_OTP, RestClient.Companion.Method.POST, postBody, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                if (response != null) {
                    //{"result":0,"resultCode":"200","pinCode":"053915","resultString":"success","refId":"2022523463477","value":"pin validation successful","data":{}}
                    Logger.println("Binjee - verifyOtp - onSuccess: $response");

                    val rootObj = JSONObject(response);
                    if(rootObj.has("value") && rootObj.getString("value") == "pin validation successful"){
                        EventManager.getInstance(mContext).fireEvent(EventManager.Events.BINJEE_PAYWALL_OTP_VERIFIED);

                        val status = rootObj.getString("resultString")
                        if (status == "success" || status == "User is already subscriber") {
                            Toaster.printToast(mContext, "Subscribed!")
                            mPrefs.setSubscriptionStatus("billed", PaywallBinjeeFragment.SLUG)

                            // Validated
                            mPrefs.setStreamable(true, PaywallBinjeeFragment.SLUG)
                            mPrefs.setMsisdn(msisdn!!, PaywallBinjeeFragment.SLUG)

                            if(status == "success"){
                                EventManager.getInstance(mContext).fireEvent(EventManager.Events.BINJEE_PAYWALL_SUBSCRIBED);
                                EventManager.getInstance(mContext).triggerFacebookSubscribeEvent(mPackage.name, "comedy", mPackage.name)
                            }else{
                                EventManager.getInstance(mContext).fireEvent(EventManager.Events.BINJEE_PAYWALL_ALREADY_SUBSCRIBED);
                            }

                            listener?.onSubscriptionResponse(true, response, true);
                        } else {
                            EventManager.getInstance(mContext).fireEvent(EventManager.Events.BINJEE_PAYWALL_FAILED_TO_SUBSCRIBE);
                            mPrefs.setStreamable(false, PaywallBinjeeFragment.SLUG)
                            mPrefs.setSubscriptionStatus(PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED, PaywallBinjeeFragment.SLUG)

                            listener?.onSubscriptionResponse(false, response, false);
                            Toaster.printToast(mContext, rootObj.getString("resultString"))
                        }

                    }
                    else{
                        EventManager.getInstance(mContext).fireEvent(EventManager.Events.BINJEE_PAYWALL_OTP_NOT_VERIFIED);
                        listener?.onSubscriptionResponse(false, response, false);
                        Toaster.printToast(mContext, "Failed to validate OTP")
                    }
                }
            }

            override fun onFailed(code: Int, reason: String?) {
                listener?.onSubscriptionResponse(false, null, false);
            }
        }).exec(PaywallBinjeeFragment.SLUG, null);
    }

    interface SubscribeNowListener{
        fun onSubscriptionResponse(billed: Boolean, response: String?, allowedToStream: Boolean);
    }

    interface BillingStatusCheckListener {
        fun onStatus(code: Int, status: String);
    }

    fun checkBillingStatus(msisdn: String, listener: BillingStatusCheckListener?) {
        val postBody: ArrayList<Params> = arrayListOf(
            Params("msisdn", msisdn),
            Params("channel", "APP"),
            Params("refId", "2022523463477"),
            Params("txid", "txid")
        );

        RestClient(mContext, Constants.BINJEE_API_BASE_URL + Constants.Companion.EndPoints.BINJEE_CHECK_STATUS, RestClient.Companion.Method.POST, postBody, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {

                /*{
                    "result": 0,
                    "resultCode": "402",
                    "pinCode": "",
                    "resultString": "success",
                    "refId": "2022523463477",
                    "value": "already subscriber",
                    "data": {}
                }*/


                Logger.println("BinjeePaymentHelper - checkBillingStatus: $response");


                try {
                    val rootObj = JSONObject(response)
                    if (rootObj.has("value") && rootObj.getString("value") == "already subscriber") {
                        mPrefs.setSubscriptionStatus("billed", PaywallBinjeeFragment.SLUG)
                        mPrefs.setStreamable(true, PaywallBinjeeFragment.SLUG)
                        mPrefs.setMsisdn(msisdn, PaywallBinjeeFragment.SLUG)
                        listener?.onStatus(0, "billed")
                    } else {
                        mPrefs.setSubscriptionStatus(PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED, PaywallBinjeeFragment.SLUG)
                        mPrefs.setStreamable(false, PaywallBinjeeFragment.SLUG)
                        listener?.onStatus(-1, PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailed(code: Int, reason: String?) {
                Logger.println("BinjeePaymentHelper - checkBillingStatus - onFailed: "+reason)
            }
        }).exec(PaywallBinjeeFragment.SLUG, null);
    }

    interface UnsubscribedListener {
        fun onStatus(code: Int, status: String);
    }
    fun unsubscribe(msisdn: String, listener: UnsubscribedListener?) {
        val postBody: ArrayList<Params> = arrayListOf(
            Params("msisdn", msisdn),
            Params("channel", "APP"),
            Params("refId", "2022523463477")
        );

        RestClient(mContext, Constants.BINJEE_API_BASE_URL + Constants.Companion.EndPoints.BINJEE_UNSUBSCRIBE, RestClient.Companion.Method.POST, postBody, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                /*{
                    "result": 0,
                    "resultCode": "500",
                    "pinCode": "",
                    "resultString": "success",
                    "refId": "2022523463477",
                    "value": "unsubscription successful",
                    "data": {}
                }*/
                Logger.println("BinjeePaymentHelper - unsubscribe: $response");

                try {
                    val rootObj = JSONObject(response)
                    if (rootObj.has("resultString") && rootObj.getString("resultString").equals("success")) {
                        mPrefs.setSubscriptionStatus(PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED, PaywallBinjeeFragment.SLUG)
                        mPrefs.setStreamable(false, PaywallBinjeeFragment.SLUG)
                        mPrefs.setMsisdn(msisdn, PaywallBinjeeFragment.SLUG)
                        listener?.onStatus(0, PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED)
                    }else {
                        listener?.onStatus(-1, PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailed(code: Int, reason: String?) {
                Logger.println("BinjeePaymentHelper - unsubscribe - onFailed: "+reason)
            }
        }).exec(PaywallBinjeeFragment.SLUG, null);
    }
}