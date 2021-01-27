package com.dmdmax.goonj.screens.activities

import android.content.Intent
import android.os.Bundle
import android.provider.SyncStateContract
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.network.responses.Params
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.screens.fragments.ChannelsFragment
import com.dmdmax.goonj.screens.views.VerificationView
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import org.json.JSONObject

class VerificationActivity : BaseActivity(), VerificationView.Listener {

    private lateinit var mView: VerificationView;
    private lateinit var mHelper: PaymentHelper;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = getCompositionRoot().getViewFactory().getVerificationImpl(null);
        setContentView(mView.getRootView());
        initialize();
    }

    private fun initialize(){
        mView.initialize(intent.getStringExtra("msisdn").toString());

        mHelper = PaymentHelper(this, PaymentHelper.PAYMENT_TELENOR);
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
        mHelper.verifyOtp(mView.getPrefs().getMsisdn(ChannelsFragment.SLUG), otp, mView.getPrefs().getSubscribedPackageId(ChannelsFragment.SLUG), object : PaymentHelper.VerifyOtpListener{
            override fun verifyOtp(verified: Boolean, response: String?) {
                if(verified && response != null){
                    startActivity(Intent(this@VerificationActivity, UserContentPrefsActivity::class.java));
                    finishAffinity();
                }else{
                    mView.getLogger().println("Verify - OTP -  Failed to verify OTP");
                }
            }
        })
    }

    override fun goBack() {
        finish();
    }
}