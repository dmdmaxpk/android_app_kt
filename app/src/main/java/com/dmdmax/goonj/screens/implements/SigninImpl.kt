package com.dmdmax.goonj.screens.implements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.models.PackageModel
import com.dmdmax.goonj.payments.BinjeePaymentHelper
import com.dmdmax.goonj.payments.ComedyPaymentHelper
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.screens.fragments.paywall.PaywallBinjeeFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment
import com.dmdmax.goonj.screens.views.LoginView
import com.dmdmax.goonj.screens.views.SigninView

class SigninImpl: BaseObservableView<SigninView.Listener>, SigninView, View.OnClickListener {

    private lateinit var mMobileNumber: EditText;
    private lateinit var mNext: FrameLayout;
    private lateinit var mBAckArrow: ImageButton;
    private lateinit var mScreenTitle: TextView;
    private lateinit var mHelp: TextView;
    private lateinit var mPrivacyPolicy: TextView;

    private lateinit var mComedyHelper: ComedyPaymentHelper;
    private lateinit var mBinjeeHelper: BinjeePaymentHelper;
    private lateinit var mPaymentHelper: PaymentHelper;

    private var mSubscriptionSource: String? = null;
    private var mPackage: PackageModel? = null;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.activity_signin, parent, false));
    }

    override fun initialize(subscriptionSource: String?, packageModel: PackageModel) {
        this.mPackage = packageModel;
        this.mSubscriptionSource = subscriptionSource;

        mMobileNumber = findViewById(R.id.mobile_number_et);

        mNext = findViewById(R.id.next);
        mNext.setOnClickListener(this);

        mBAckArrow = findViewById(R.id.back_arrow);
        mBAckArrow.setOnClickListener(this);

        mScreenTitle = findViewById(R.id.screen_title);
        mScreenTitle.text = getString(R.string.sing_in)

        mHelp = findViewById(R.id.help);
        mHelp.setOnClickListener(this);

        mPrivacyPolicy = findViewById(R.id.privacy_policy);
        mPrivacyPolicy.setOnClickListener(this);

        mPaymentHelper = PaymentHelper(getContext(), PaymentHelper.PAYMENT_TELENOR);
        mBinjeeHelper = BinjeePaymentHelper(getContext());
        mComedyHelper = ComedyPaymentHelper(getContext());
    }

    override fun onClick(v: View?) {
        when(v){
            mNext -> {
                val mobileNumber: String = mMobileNumber.text.toString();
                if(mobileNumber!!.isNotEmpty() && mobileNumber.startsWith("03") && mobileNumber.length == 11){

                    if(mSubscriptionSource != null && mSubscriptionSource.equals(PaywallBinjeeFragment.SLUG)){
                        mBinjeeHelper.sendOtp(mobileNumber);
                    }else if(mSubscriptionSource != null && mSubscriptionSource.equals(PaywallComedyFragment.SLUG)){
                        mComedyHelper.sendOtp(mobileNumber);
                    }else{
                        mPaymentHelper.sendOtp(mobileNumber, mPackage);
                    }

                    for (listener in getListeners()) {
                        listener.next(mobileNumber);
                    }
                }else{
                    getToaster().printToast(getContext(), "Please provide valid telenor mobile number");
                }
            }

            mBAckArrow -> {
                for (listener in getListeners()) {
                    listener.goBack();
                }
            }

            mHelp -> {
                for (listener in getListeners()) {
                    listener.help();
                }
            }

            mPrivacyPolicy -> {
                for (listener in getListeners()) {
                    listener.viewPrivacyPolicy();
                }
            }
        }
    }
}