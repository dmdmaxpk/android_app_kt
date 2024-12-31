package com.dmdmax.goonj.screens.activities

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.models.PackageModel
import com.dmdmax.goonj.models.Params
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.payments.BinjeePaymentHelper
import com.dmdmax.goonj.payments.ComedyPaymentHelper
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.screens.dialogs.DialogManager
import com.dmdmax.goonj.screens.fragments.paywall.PaywallBinjeeFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.views.VerificationView
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Toaster
import org.json.JSONObject

class VerificationActivity : BaseActivity(), VerificationView.Listener {

    private lateinit var mView: VerificationView;
    private lateinit var mHelper: PaymentHelper;
    private lateinit var mBinjeeHelper: BinjeePaymentHelper;
    private lateinit var mComedyHelper: ComedyPaymentHelper;
    private lateinit var msisdn: String;

    private var mSubscriptionSource: String? = null;
    private var mPackageModel: PackageModel? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = getCompositionRoot().getViewFactory().getVerificationImpl(null);
        setContentView(mView.getRootView());
        initialize();
        EventManager.getInstance(this).fireEvent("OTP_Verification ${EventManager.Events.VIEW}");
    }

    private fun initialize(){
        mPackageModel = intent.getSerializableExtra(PaywallGoonjFragment.ARGS_DEFAULT_PACKAGE) as PackageModel?;
        mSubscriptionSource = intent.getStringExtra(PaywallGoonjFragment.ARG_SUBSCRIPTION_SOURCE);
        msisdn = intent.getStringExtra("msisdn").toString();

        mView.initialize(intent.getStringExtra("msisdn").toString(), mSubscriptionSource);

        mHelper = PaymentHelper(this, PaymentHelper.PAYMENT_TELENOR);
        mBinjeeHelper = BinjeePaymentHelper(this);
        mComedyHelper = ComedyPaymentHelper(this);
    }

    override fun onStart() {
        super.onStart();
        mView.registerListener(this);
    }

    override fun onStop() {
        super.onStop();
        mView.unregisterListener(this);
    }

    override fun verify(otp: String) {
        //SendOtp - onSuccess - {"code":0,"data":"OTP sent","gw_transaction_id":"gw_logger-13bw36hkk6gf8zt-2021-01-21,06:08"}
        //Verify - OTP - {"code":7,"data":"OTP Validated!","access_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtc2lzZG4iOiIwMzQ3NjczMzc2NyIsImlhdCI6MTYxMTIwOTM5MCwiZXhwIjoxNjExMjA5NDIwfQ.SwkC1Sax-a9YIu35XX-EjH1KdrII6k6smsC-pvo-XoQ","refresh_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtc2lzZG4iOiIwMzQ3NjczMzc2NyIsImlhdCI6MTYxMTIwOTM5MH0.B4lWH11BWPGH6yiHnCna-jNh8hlBtDWs5WW8RAIINJs","subscription_status":"expired","is_allowed_to_stream":false,"user_id":"fGmTkKjHYmuN","subscribed_package_id":"QDfG","gw_transaction_id":"gw_logger-13bw36hkk6ggxwp-2021-01-21,06:09","subscribed_packages":[{"_id":"cmBwbrtITlp1","subscribed_package_id":"QDfG"}]}
        Logger.println("verify: $otp");

        if(mSubscriptionSource != null && mSubscriptionSource.equals(PaywallBinjeeFragment.SLUG)){
            mBinjeeHelper.verifyOtp(msisdn, otp, mPackageModel!!, object : BinjeePaymentHelper.SubscribeNowListener{
                override fun onSubscriptionResponse(billed: Boolean, response: String?, allowedToStream: Boolean) {
                    onResponse(otp, true, response, allowedToStream);
                }
            })
        }else if(mSubscriptionSource != null && mSubscriptionSource.equals(PaywallComedyFragment.SLUG)){
            mComedyHelper.verifyOtp(msisdn, otp, mPackageModel!!, object : ComedyPaymentHelper.VerifyOtpListener{
                override fun verifyOtp(verified: Boolean, response: String?, allowedToStream: Boolean) {
                    onResponse(otp, verified, response, allowedToStream);
                }
            })
        }else{
            Logger.println("verifying: $otp");

            mHelper.verifyOtp(msisdn, otp, mView.getPrefs().getSubscribedPackageId(PaywallGoonjFragment.SLUG), object : PaymentHelper.VerifyOtpListener{
                override fun verifyOtp(verified: Boolean, response: String?, allowedToStream: Boolean) {
                    Logger.println("verifyOtp: $verified - response - $response");
                    onResponse(otp, verified, response, allowedToStream);
                }
            })
        }
    }

    private fun onResponse(otp: String, verified: Boolean, response: String?, allowedToStream: Boolean){

        var triggerCmsFlow = false;

        if(verified) {
            if(response != null) {
                if(allowedToStream && JSONObject(response).has("subscription_status") && (JSONObject(response).getString("subscription_status").equals("billed") || JSONObject(response).getString("subscription_status").equals("trial"))){
                    mView.getPrefs().setMsisdn(msisdn, PaywallGoonjFragment.SLUG)

                    if(PlayerActivity.ARGS_CHANNEL != null || PlayerActivity.ARGS_VIDEO != null){
                        getCompositionRoot().getViewFactory().toPlayerScreen(null, null);
                    }
                    Toaster.printToast(this@VerificationActivity, "Signed in successfully");
                    finish();
                }else{
                    triggerCmsFlow = true;
                }
            }else {
                triggerCmsFlow = true;
            }

            if(triggerCmsFlow) {

                // flow for consent management system
                val postBody = arrayListOf<Params>();
                postBody.add(Params("msisdn", msisdn));
                postBody.add(Params("serviceId", mPackageModel?.serviceId));

                RestClient(this@VerificationActivity, Constants.API_BASE_URL + Constants.Companion.EndPoints.CMS_TOKEN, RestClient.Companion.Method.POST, postBody, object : NetworkOperationListener {
                    override fun onSuccess(response: String?) {

                        Logger.println("onSuccess: $response");

                        val rootObj = JSONObject(response);
                        val token = rootObj.getJSONObject("response").getString("token");

                        val dialog: AlertDialog = DialogManager().getCMSDialog(this@VerificationActivity, token, object : DialogManager.RedirectListener {
                            override fun onRedirect(respCode: String) {
                                Logger.println("Response Code: ${respCode}")
                                when (respCode) {
                                    "00", "03" -> {
                                        // 00 > success /trial | 03 - > already exist
                                        mView.getPrefs().setMsisdn(msisdn, PaywallGoonjFragment.SLUG)
                                        mView.getPrefs().setStreamable(true, PaywallGoonjFragment.SLUG)
                                        mView.getPrefs().setSubscriptionStatus(PaymentHelper.Companion.PaymentStatus.STATUS_BILLED, PaywallGoonjFragment.SLUG)

                                        if(PlayerActivity.ARGS_CHANNEL != null || PlayerActivity.ARGS_VIDEO != null){
                                            getCompositionRoot().getViewFactory().toPlayerScreen(null, null);
                                        }
                                        Toaster.printToast(this@VerificationActivity, "Signed in successfully");
                                        finish();

                                    }
                                    "02" -> {
                                        // low balance
                                        mView.getPrefs().setMsisdn(msisdn, PaywallGoonjFragment.SLUG)
                                        mView.getPrefs().setStreamable(false, PaywallGoonjFragment.SLUG)
                                        mView.getPrefs().setSubscriptionStatus(PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED, PaywallGoonjFragment.SLUG)

                                        Toaster.printToast(this@VerificationActivity, "You don't have sufficient balance, try again later");
                                        finish();

                                    }
                                    "06" -> {
                                        // trial activated
                                        mView.getPrefs().setMsisdn(msisdn, PaywallGoonjFragment.SLUG)
                                        mView.getPrefs().setStreamable(true, PaywallGoonjFragment.SLUG)
                                        mView.getPrefs().setSubscriptionStatus(PaymentHelper.Companion.PaymentStatus.STATUS_BILLED, PaywallGoonjFragment.SLUG)

                                        if(PlayerActivity.ARGS_CHANNEL != null || PlayerActivity.ARGS_VIDEO != null){
                                            getCompositionRoot().getViewFactory().toPlayerScreen(null, null);
                                        }
                                        Toaster.printToast(this@VerificationActivity, "Signed in successfully");
                                        finish();

                                    }
                                    else -> {
                                        // error
                                        mView.getPrefs().setMsisdn(msisdn, PaywallGoonjFragment.SLUG)
                                        mView.getPrefs().setStreamable(false, PaywallGoonjFragment.SLUG)
                                        mView.getPrefs().setSubscriptionStatus(PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED, PaywallGoonjFragment.SLUG)

                                        Toaster.printToast(this@VerificationActivity, "Sing-in failed");
                                        finish();
                                    }
                                }
                            }
                        });
                        dialog.show();

                    }

                    override fun onFailed(code: Int, reason: String?) {
                        TODO("Not yet implemented")
                        Logger.println("onFailed: " + reason);
                    }
                }).exec();

                // trigger billing
//                val paymentSource: String? = if(intent.extras != null && intent?.extras?.containsKey(PaywallGoonjFragment.ARG_PAYMENT_SOURCE) == true) intent?.extras?.getString(PaywallGoonjFragment.ARG_PAYMENT_SOURCE) else null;
//                mHelper.subscribeNow(msisdn, mPackageModel!!, paymentSource!!, otp, object: PaymentHelper.SubscribeNowListener{
//                    override fun onSubscriptionResponse(billed: Boolean, response: String?, allowedToStream: Boolean) {
//                        //Logger.println("subscribeNow: $response");
//                        if(billed && allowedToStream){
//                            mView.getPrefs().setMsisdn(msisdn, PaywallGoonjFragment.SLUG)
//                            if(PlayerActivity.ARGS_CHANNEL != null || PlayerActivity.ARGS_VIDEO != null){
//                                getCompositionRoot().getViewFactory().toPlayerScreen(null, null);
//                            }
//                            val rootObj = JSONObject(response);
//                            val code = rootObj.getInt("code")
//                            Toaster.printToast(this@VerificationActivity, if(code == 11) "Signed In Successfully" else "Signed In Successfully");
//                            finish()
//                        }else{
//                            Toaster.printToast(this@VerificationActivity, "Failed to sing in, please try again in few minutes.")
//                        }
//                    }
//                });
            }
        } else{
            mView.getToaster().printToast(this@VerificationActivity, "Wrong OTP entered.")
            mView.getLogger().println("Verify - OTP -  Failed to verify OTP");
        }
    }

    override fun goBack() {
        finish();
    }
}