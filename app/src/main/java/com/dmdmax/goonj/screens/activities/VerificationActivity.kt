package com.dmdmax.goonj.screens.activities

import android.os.Bundle
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.models.PackageModel
import com.dmdmax.goonj.payments.BinjeePaymentHelper
import com.dmdmax.goonj.payments.ComedyPaymentHelper
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.screens.fragments.paywall.PaywallBinjeeFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.views.VerificationView
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Toaster

class VerificationActivity : BaseActivity(), VerificationView.Listener {

    private lateinit var mView: VerificationView;
    private lateinit var mHelper: PaymentHelper;
    private lateinit var mBinjeeHelper: BinjeePaymentHelper;
    private lateinit var mComedyHelper: ComedyPaymentHelper;

    private var mSubscriptionSource: String? = null;
    private var mPackageModel: PackageModel? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = getCompositionRoot().getViewFactory().getVerificationImpl(null);
        setContentView(mView.getRootView());
        initialize();
        EventManager.getInstance(this).fireEvent("OTP_Verification${EventManager.Events.VIEW}");
    }

    private fun initialize(){
        mPackageModel = intent.getSerializableExtra(PaywallGoonjFragment.ARGS_DEFAULT_PACKAGE) as PackageModel?;
        mSubscriptionSource = intent.getStringExtra(PaywallGoonjFragment.ARG_SUBSCRIPTION_SOURCE);
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

        if(mSubscriptionSource != null && mSubscriptionSource.equals(PaywallBinjeeFragment.SLUG)){
            mBinjeeHelper.verifyOtp(mView.getPrefs().getMsisdn(PaywallBinjeeFragment.SLUG), otp, mPackageModel!!, object : BinjeePaymentHelper.SubscribeNowListener{
                override fun onSubscriptionResponse(billed: Boolean, response: String?, allowedToStream: Boolean) {
                    onResponse(otp, true, response, allowedToStream);
                }
            })
        }else if(mSubscriptionSource != null && mSubscriptionSource.equals(PaywallComedyFragment.SLUG)){
            mComedyHelper.verifyOtp(mView.getPrefs().getMsisdn(PaywallComedyFragment.SLUG), otp, mPackageModel!!, object : ComedyPaymentHelper.VerifyOtpListener{
                override fun verifyOtp(verified: Boolean, response: String?, allowedToStream: Boolean) {
                    onResponse(otp, verified, response, allowedToStream);
                }
            })
        }else{
            mHelper.verifyOtp(mView.getPrefs().getMsisdn(PaywallGoonjFragment.SLUG), otp, mView.getPrefs().getSubscribedPackageId(PaywallGoonjFragment.SLUG), object : PaymentHelper.VerifyOtpListener{
                override fun verifyOtp(verified: Boolean, response: String?, allowedToStream: Boolean) {
                    onResponse(otp, verified, response, allowedToStream);
                }
            })
        }
    }

    private fun onResponse(otp: String, verified: Boolean, response: String?, allowedToStream: Boolean){
        if(verified && response != null){
            if(allowedToStream){
                if(PlayerActivity.ARGS_CHANNEL != null || PlayerActivity.ARGS_VIDEO != null){
                    getCompositionRoot().getViewFactory().toPlayerScreen(null, null);
                }
                Toaster.printToast(this@VerificationActivity, "Subscribed successfully");
                finish();
            }else{
                if(verified){
                    // subscribe now
                    val paymentSource: String? = if(intent.extras != null && intent?.extras?.containsKey(PaywallGoonjFragment.ARG_PAYMENT_SOURCE) == true) intent?.extras?.getString(PaywallGoonjFragment.ARG_PAYMENT_SOURCE) else null;

                    mHelper.subscribeNow(mView.getPrefs().getMsisdn(PaywallGoonjFragment.SLUG), mPackageModel!!, paymentSource!!, otp, object: PaymentHelper.SubscribeNowListener{
                        override fun onSubscriptionResponse(billed: Boolean, response: String?, allowedToStream: Boolean) {
                            Logger.println("subscribeNow: $response");
                            if(billed && allowedToStream){
                                if(PlayerActivity.ARGS_CHANNEL != null || PlayerActivity.ARGS_VIDEO != null){
                                    getCompositionRoot().getViewFactory().toPlayerScreen(null, null);
                                }
                                Toaster.printToast(this@VerificationActivity, "Subscribed successfully");
                                finish()
                            }else{
                                Toaster.printToast(this@VerificationActivity, "Failed to subscribe, please check your balance and try agian.")
                            }
                        }
                    });
                }
            }
        }else{
            mView.getToaster().printToast(this@VerificationActivity, "Wrong OTP entered.")
            mView.getLogger().println("Verify - OTP -  Failed to verify OTP");
        }
    }

    override fun goBack() {
        finish();
    }
}