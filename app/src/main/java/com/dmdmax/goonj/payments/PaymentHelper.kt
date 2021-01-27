package com.dmdmax.goonj.payments

import android.content.Context
import android.content.Intent
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.network.responses.Params
import com.dmdmax.goonj.network.responses.PaywallPackage
import com.dmdmax.goonj.screens.activities.UserContentPrefsActivity
import com.dmdmax.goonj.screens.fragments.ChannelsFragment
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Toaster
import org.json.JSONObject

class PaymentHelper {
    private lateinit var mContext: Context;
    private lateinit var mPrefs: GoonjPrefs;
    private val source = "app";
    private var mPaymentSource: String;

    companion object {
        const val PAYMENT_TELENOR = "telenor"
        const val PAYMENT_EASYPAISA = "easypaisa"
    }

    constructor(context: Context, paymentSource: String) {
        this.mContext = context;
        this.mPaymentSource = paymentSource;
        this.mPrefs = GoonjPrefs(context);
    }

    interface VerifyOtpListener{
        fun verifyOtp(verified: Boolean, response: String?);
    }

    fun sendOtp(msisdn: String, packageId: String){
        val mList: ArrayList<Params> = arrayListOf();
        mList.add(Params("msisdn", msisdn));
        mList.add(Params("source", source));
        mList.add(Params("package_id", packageId));
        mList.add(Params("payment_source", mPaymentSource));

        RestClient(mContext, Constants.API_BASE_URL + Constants.Companion.EndPoints.SEND_OTP, RestClient.Companion.Method.POST, mList, object: NetworkOperationListener {
            override fun onSuccess(response: String?) {
                Logger.println("SendOtp - onSuccess - "+ response);
                mPrefs.setMsisdn(msisdn, ChannelsFragment.SLUG);
                mPrefs.setSubscribedPackageId(packageId, ChannelsFragment.SLUG);
            }

            override fun onFailed(code: Int, reason: String?) {
                Logger.println("SendOtp - onFailed - "+ reason);
            }
        }).exec();
    }

    fun verifyOtp(msisdn: String?, otp: String?, packageId: String?, listener: VerifyOtpListener?){
        val args: ArrayList<Params> = arrayListOf();
        args.add(Params("msisdn", msisdn));
        args.add(Params("otp", otp));
        args.add(Params("package_id", packageId));
        args.add(Params("payment_source", mPaymentSource));

        RestClient(mContext, Constants.API_BASE_URL + Constants.Companion.EndPoints.VERIFY_OTP, RestClient.Companion.Method.POST, args, object : NetworkOperationListener{
            override fun onSuccess(response: String?) {
                if(response != null){
                    val rootObj = JSONObject(response);
                    if(rootObj.getInt("code") == 7){
                        // validated
                        Logger.println("Verify - OTP - "+response);
                        Toaster.printToast(mContext, rootObj.getString("data"));
                        mPrefs.setOtpValidated(true);
                        mPrefs.setAccessToken(rootObj.getString("access_token"));
                        mPrefs.setRefreshToken(rootObj.getString("refresh_token"));

                        if(rootObj.has("subscription_status"))
                            mPrefs.setSubscriptionStatus(rootObj.getString("subscription_status"), ChannelsFragment.SLUG);

                        if(rootObj.has("is_allowed_to_stream"))
                            mPrefs.setStreamable(rootObj.getBoolean("is_allowed_to_stream"), ChannelsFragment.SLUG);

                        if(rootObj.has("user_id"))
                            mPrefs.setUserId(rootObj.getString("user_id"));

                        if(rootObj.has("subscribed_package_id"))
                            mPrefs.setSubscribedPackageId(rootObj.getString("subscribed_package_id"), ChannelsFragment.SLUG);

                        listener?.verifyOtp(true, response)
                    }else{
                        mPrefs.setOtpValidated(false);
                        listener?.verifyOtp(false, response)
                        Toaster.printToast(mContext, rootObj.getString("message"));
                    }
                }
            }

            override fun onFailed(code: Int, reason: String?) {
                listener?.verifyOtp(true, null)
            }
        }).exec();
    }

    fun initPayment(paymentSource: String, msisdn: String?, mPackage: PaywallPackage, slug: String) {

    }
}