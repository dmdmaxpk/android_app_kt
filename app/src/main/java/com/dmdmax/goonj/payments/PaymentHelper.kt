package com.dmdmax.goonj.payments

import android.content.Context
import com.dmdmax.goonj.network.responses.PaywallPackage
import com.dmdmax.goonj.storage.GoonjPrefs

class PaymentHelper {
    private var mContext: Context? = null;
    private var mPrefs: GoonjPrefs? = null;


    fun PaymentHelper(context: Context?) {
        mContext = context
        mPrefs = GoonjPrefs(context)
    }

    fun initPayment(paymentSource: String, msisdn: String?, mPackage: PaywallPackage, slug: String) {

    }
}