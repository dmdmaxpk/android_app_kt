package com.dmdmax.goonj.screens.implements

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.screens.views.VerificationView

class VerificationImpl: BaseObservableView<VerificationView.Listener>, VerificationView, View.OnClickListener {


    private lateinit var mVerificationCodeText: TextView;
    private lateinit var mVerify: FrameLayout;
    private lateinit var mBAckArrow: ImageButton;

    private lateinit var mEt1: EditText;
    private lateinit var mEt2: EditText;
    private lateinit var mEt3: EditText;
    private lateinit var mEt4: EditText;
    private lateinit var mWatcher: MyTextWathcer;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.activity_verification, parent, false));
    }

    override fun initialize(msisdn: String) {
        mVerificationCodeText = findViewById(R.id.verification_code_txt);
        mVerify = findViewById(R.id.verify);
        mVerify.setOnClickListener(this);

        mVerificationCodeText.text = "Enter verification code received in\nSMS on $msisdn";

        mBAckArrow = findViewById(R.id.back_arrow);
        mBAckArrow.setOnClickListener(this);

        mEt1 = findViewById(R.id.et1);
        mEt2 = findViewById(R.id.et2);
        mEt3 = findViewById(R.id.et3);
        mEt4 = findViewById(R.id.et4);

        mWatcher = MyTextWathcer();
        mEt1.addTextChangedListener(mWatcher)
        mEt2.addTextChangedListener(mWatcher)
        mEt3.addTextChangedListener(mWatcher)
        mEt4.addTextChangedListener(mWatcher)
    }

    override fun onClick(v: View?) {
        when(v){
            mVerify -> {
                if (mEt1.text != null && mEt1.length() == 1 && mEt2.text != null && mEt2.length() == 1 && mEt3.text != null && mEt3.length() == 1 && mEt4.text != null && mEt4.length() == 1) {
                    val strOtp: String = mEt1.text.toString() + mEt2.text.toString() + mEt3.text.toString() + mEt4.text.toString()
                    for (listener in getListeners()) {
                        listener.verify(strOtp);
                    }
                }else{
                    getToaster().printToast(getContext(), "Please enter valid OTP");
                }
            }

            mBAckArrow -> {
                for (listener in getListeners()) {
                    listener.goBack();
                }
            }
        }
    }

    private inner class MyTextWathcer : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            if (editable.length == 1) {
                if (mEt1.length() == 1) {
                    mEt2.requestFocus()
                }
                if (mEt2.length() == 1) {
                    mEt3.requestFocus()
                }
                if (mEt3.length() == 1) {
                    mEt4.requestFocus()
                }
            } else if (editable.isEmpty()) {
                if (mEt4.length() == 0) {
                    mEt3.requestFocus()
                }
                if (mEt3.length() == 0) {
                    mEt2.requestFocus()
                }
                if (mEt2.length() == 0) {
                    mEt1.requestFocus()
                }
            }
        }
    }
}