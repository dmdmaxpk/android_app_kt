package com.dmdmax.goonj.payments

import android.content.Context
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.models.PackageModel
import com.dmdmax.goonj.models.Params
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Toaster
import org.json.JSONObject

class PaymentHelper {
    private lateinit var mContext: Context;
    private lateinit var mPrefs: GoonjPrefs;
    private val source = "app";
    private var mPaymentSource: String?;

    companion object {
        const val PAYMENT_TELENOR = "telenor"
        const val PAYMENT_EASYPAISA = "easypaisa"

        object PaymentStatus {
            const val STATUS_TRIAL = "trial"
            const val STATUS_BILLED = "billed"
            const val STATUS_GRACED = "graced"
            const val STATUS_EXPIRED = "expired"
            const val NOT_SUBSCRIBED = "not_billed"
        }
    }

    constructor(context: Context, paymentSource: String?) {
        this.mContext = context;
        this.mPaymentSource = paymentSource;
        this.mPrefs = GoonjPrefs(context);
    }

    interface VerifyOtpListener{
        fun verifyOtp(verified: Boolean, response: String?, allowedToStream: Boolean);
    }

    fun sendOtp(msisdn: String, packageModel: PackageModel?){
        val mList: ArrayList<Params> = arrayListOf();
        mList.add(Params("msisdn", msisdn));
        mList.add(Params("source", source));
        mList.add(Params("package_id", packageModel?.id));
        mList.add(Params("payment_source", mPaymentSource));
        mList.add(Params("fcm_token", mPrefs.getFcmToken()));

        RestClient(mContext, Constants.API_BASE_URL + Constants.Companion.EndPoints.SEND_OTP, RestClient.Companion.Method.POST, mList, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                Logger.println("SendOtp - onSuccess - $response");
                mPrefs.setMsisdn(msisdn, PaywallGoonjFragment.SLUG);
                mPrefs.setSubscribedPackageId(packageModel?.id, PaywallGoonjFragment.SLUG);

                EventManager.getInstance(mContext).fireEvent(EventManager.Events.GOONJ_PAYWALL_OTP_SENT)
            }

            override fun onFailed(code: Int, reason: String?) {
                Logger.println("SendOtp - onFailed - $reason");
            }
        }).exec();
    }

    fun verifyOtp(msisdn: String?, otp: String?, packageId: String?, listener: VerifyOtpListener?){
        val args: ArrayList<Params> = arrayListOf();
        args.add(Params("msisdn", msisdn));
        args.add(Params("otp", otp));
        args.add(Params("package_id", packageId));
        args.add(Params("payment_source", mPaymentSource));

        RestClient(mContext, Constants.API_BASE_URL + Constants.Companion.EndPoints.VERIFY_OTP, RestClient.Companion.Method.POST, args, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                if (response != null) {
                    val rootObj = JSONObject(response);
                    if (rootObj.getInt("code") == 7) {
                        EventManager.getInstance(mContext).fireEvent(EventManager.Events.GOONJ_PAYWALL_OTP_VERIFIED);

                        // validated
                        Logger.println("Verify - OTP - " + response);
                        Toaster.printToast(mContext, rootObj.getString("data"));
                        mPrefs.setOtpValidated(true);
                        mPrefs.setAccessToken(rootObj.getString("access_token"));
                        mPrefs.setRefreshToken(rootObj.getString("refresh_token"));

                        if (rootObj.has("subscription_status"))
                            mPrefs.setSubscriptionStatus(rootObj.getString("subscription_status"), PaywallGoonjFragment.SLUG);

                        val isStreamable = (rootObj.has("is_allowed_to_stream") && rootObj.getBoolean("is_allowed_to_stream"));
                        mPrefs.setStreamable(isStreamable, PaywallGoonjFragment.SLUG)

                        if(isStreamable){
                            EventManager.getInstance(mContext).fireEvent(EventManager.Events.GOONJ_PAYWALL_ALREADY_SUBSCRIBED);
                        }

                        if (rootObj.has("user_id"))
                            mPrefs.setUserId(rootObj.getString("user_id"), PaywallGoonjFragment.SLUG);

                        if (rootObj.has("subscribed_package_id"))
                            mPrefs.setSubscribedPackageId(rootObj.getString("subscribed_package_id"), PaywallGoonjFragment.SLUG);

                        listener?.verifyOtp(true, response, isStreamable)
                    } else {
                        EventManager.getInstance(mContext).fireEvent(EventManager.Events.GOONJ_PAYWALL_OTP_NOT_VERIFIED);

                        mPrefs.setOtpValidated(false);
                        listener?.verifyOtp(false, response, false)
                        //Toaster.printToast(mContext, rootObj.getString("message"));
                    }
                }
            }

            override fun onFailed(code: Int, reason: String?) {
                listener?.verifyOtp(true, null, false)
            }
        }).exec();
    }

    interface SubscribeNowListener{
        fun onSubscriptionResponse(billed: Boolean, response: String?, allowedToStream: Boolean);
    }

    fun subscribeNow(msisdn: String?, mPackage: PackageModel, paymentSource: String, otp: String?, listener: SubscribeNowListener) {

        val postBody = arrayListOf<Params>();
        postBody.add(Params("msisdn", msisdn))
        postBody.add(Params("package_id", mPackage.id))
        postBody.add(Params("source", "app"))
        postBody.add(Params("payment_source", paymentSource))

        if (paymentSource == PAYMENT_EASYPAISA && otp != null) {
            postBody.add(Params("otp", otp))
        }

        RestClient(mContext, Constants.API_BASE_URL + Constants.Companion.EndPoints.SUBSCRIBE, RestClient.Companion.Method.POST, postBody, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {

                Logger.println("onSuccess: " + response);

                val rootObj = JSONObject(response);
                val code = rootObj.getInt("code")
                var streamable = false;

                if (code == 0 || code == 11 || code == 9) {
                    mPrefs.setOtpValidated(true)
                    if (rootObj.has("package_id")) {
                        mPrefs.setSubscribedPackageId(rootObj.getString("package_id"), PaywallGoonjFragment.SLUG)
                    }
                    var status = "billed";
                    if(code == 11){
                        streamable = true;
                        status = "trial"
                        EventManager.getInstance(mContext).fireEvent(EventManager.Events.GOONJ_PAYWALL_TRIAL_ACTIVATED);
                    }
                    mPrefs.setMsisdn(msisdn!!, PaywallGoonjFragment.SLUG);
                    mPrefs.setSubscriptionStatus(status, PaywallGoonjFragment.SLUG)


                    if(rootObj.has("is_allowed_to_stream")){
                        streamable = rootObj.getBoolean("is_allowed_to_stream");
                    }else if(code == 0 && rootObj.has("message")){
                        EventManager.getInstance(mContext).fireEvent("${EventManager.Events.GOONJ_PAYWALL_MESSAGE}${rootObj.getString("message").replace(" ", "_")}");

                        //{"code":0,"message":"Package successfully switched.","gw_transaction_id":"gw_logger-5fyhkp89j9tx-2021-05-28,11:49"}
                        Toaster.printToast(mContext, "Sign in success.");
                        streamable = true;
                    }else{
                        if(code == 0){
                            EventManager.getInstance(mContext).fireEvent(EventManager.Events.GOONJ_PAYWALL_SUBSCRIBED);
                            EventManager.getInstance(mContext).triggerFacebookSubscribeEvent(mPackage.name, "goonj", paymentSource)
                        }else if(code == 9){
                            EventManager.getInstance(mContext).fireEvent(EventManager.Events.GOONJ_PAYWALL_ALREADY_SUBSCRIBED);
                            Toaster.printToast(mContext, "Sign in success!")
                            streamable = true;
                        }
                    }

                    listener.onSubscriptionResponse(true, response, streamable)
                }else{
                    EventManager.getInstance(mContext).fireEvent(EventManager.Events.GOONJ_PAYWALL_FAILED_TO_SUBSCRIBE);
                    listener.onSubscriptionResponse(false, response, false)
                }

            }

            override fun onFailed(code: Int, reason: String?) {
                TODO("Not yet implemented")
                Logger.println("onFailed: " + reason);
            }
        }).exec();
    }

    interface BillingStatusCheckListener {
        fun onStatus(code: Int, status: String);
    }

    fun checkBillingStatus(msisdn: String, listener: BillingStatusCheckListener?) {
        val packageId = mPrefs.getSubscribedPackageId(PaywallGoonjFragment.SLUG);
        Logger.println("PACKAGE ID ${packageId}")
        val params = arrayListOf<Params>()
        params.add(Params("msisdn", msisdn));
        params.add(Params("package_id", packageId));

        RestClient(mContext, Constants.API_BASE_URL + Constants.Companion.EndPoints.CHECK_GOONJ_SUBSCRIPTION, RestClient.Companion.Method.POST, params, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                Logger.println("PaymentHelper - checkBillingStatus: $response");

                val rootObj = JSONObject(response);
                val code: Int = rootObj.getInt("code")

                if (code == 0) {
                    val status: String = rootObj.getJSONObject("data").getString("subscription_status")
                    mPrefs.setSubscribedPackageId(rootObj.getString("subscribed_package_id"), PaywallGoonjFragment.SLUG)
                    mPrefs.setStreamable(rootObj.getJSONObject("data").has("is_allowed_to_stream") && rootObj.getJSONObject("data").getBoolean("is_allowed_to_stream"), PaywallGoonjFragment.SLUG)
                    mPrefs.setSubscriptionStatus(status, PaywallGoonjFragment.SLUG)
                    mPrefs.setUserId(rootObj.getJSONObject("data").getString("user_id"), PaywallGoonjFragment.SLUG)
                    listener?.onStatus(code, status)
                } else {
                    mPrefs.setSubscriptionStatus("not_billed", PaywallGoonjFragment.SLUG);
                    listener?.onStatus(code, PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED)
                }
            }

            override fun onFailed(code: Int, reason: String?) {
                TODO("Not yet implemented")
            }
        }).exec();
    }
}