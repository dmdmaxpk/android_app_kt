package com.dmdmax.goonj.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.dmdmax.goonj.R
import com.dmdmax.goonj.models.PackageModel
import com.dmdmax.goonj.models.Params
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.payments.BinjeePaymentHelper
import com.dmdmax.goonj.payments.ComedyPaymentHelper
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.screens.activities.PaywallActivity
import com.dmdmax.goonj.screens.dialogs.DialogManager
import com.dmdmax.goonj.screens.fragments.paywall.PaywallBinjeeFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Toaster
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class PackageListAdapter: BaseAdapter {

    private lateinit var mPackagesList: List<PackageModel>;
    private var slug: String;
    private var mContext: Context;
    private var mPrefs: GoonjPrefs;

    constructor(mPackagesList: List<PackageModel>, slug: String, context: Context) {
        this.mPackagesList = mPackagesList
        this.slug = slug
        this.mContext = context;
        this.mPrefs = GoonjPrefs(mContext);
    }

    override fun getCount(): Int {
        return mPackagesList!!.size
    }

    override fun getItem(i: Int): Any? {
        return mPackagesList!![i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(mContext).inflate(R.layout.settings_package_list_item_subscribe_now, parent, false);
        val packageName = view.findViewById<TextView>(R.id.package_name);
        val smallPb = view.findViewById<ProgressBar>(R.id.progress_bar);
        val subStatusLayout = view.findViewById<FrameLayout>(R.id.subs_status_layout);
        val subsText = view.findViewById<TextView>(R.id.subs_text);
        packageName.text = mPackagesList[position].name

        Logger.println("goonj: "+mPrefs.getSubscriptionStatus(slug));
        if(slug == PaywallGoonjFragment.SLUG && (mPrefs.getSubscriptionStatus(slug).equals("billed") || mPrefs.getSubscriptionStatus(slug).equals("trial"))){
            if(mPrefs.getSubscribedPackageId(slug).equals(mPackagesList[position].id)){
                smallPb.visibility = View.GONE;
                subsText.text = "Sign-out";
                subStatusLayout.visibility = View.VISIBLE;
                subStatusLayout.background = ContextCompat.getDrawable(mContext, R.drawable.unsubscribe);
            }else{
                smallPb.visibility = View.GONE;
                subsText.text = "Sign-in Now";
                subStatusLayout.visibility = View.VISIBLE;
                subStatusLayout.background = ContextCompat.getDrawable(mContext, R.drawable.subscribe_now);
            }
        }
        else {
            if(mPrefs.getSubscriptionStatus(slug).equals("billed")){
                smallPb.visibility = View.GONE;
                subsText.text = "Sign-out";
                subStatusLayout.visibility = View.VISIBLE;
                subStatusLayout.background = ContextCompat.getDrawable(mContext, R.drawable.unsubscribe);
            }
            else{
                smallPb.visibility = View.GONE;
                subsText.text = "Sign-in Now";
                subStatusLayout.visibility = View.VISIBLE;
                subStatusLayout.background = ContextCompat.getDrawable(mContext, R.drawable.subscribe_now);
            }
        }


        subStatusLayout.setOnClickListener(View.OnClickListener {
            Logger.println("BUTTON CLICKED");
            if(slug == PaywallGoonjFragment.SLUG){
                // goonj paywall
                if(mPrefs.getSubscriptionStatus(slug).equals("billed") || mPrefs.getSubscriptionStatus(slug).equals("trial")){
                    val subPackageId: String? = mPrefs.getSubscribedPackageId(slug)
                    if (subPackageId == mPackagesList[position].id && mPrefs.isOtpValidated()) {
                        // same package id - it means just unsubscribe
                        val postBody = ArrayList<Params>()
                        postBody.add(Params("msisdn", mPrefs.getMsisdn(mPackagesList[position].slug)))
                        postBody.add(Params("source", "app"))
                        postBody.add(Params("package_id", mPackagesList[position].id))
                        RestClient(mContext, Constants.PAYWALL_BASE_URL + Constants.Companion.EndPoints.CHECK_GOONJ_SUBSCRIPTION, RestClient.Companion.Method.POST, postBody, object : NetworkOperationListener {
                                override fun onSuccess(response: String?) {
                                    smallPb.visibility = View.GONE
                                    try {
                                        val data = JSONObject(response)
                                        val code = data.getInt("code")
                                        val subStatus: String;
                                        var nextBilling: String? = null
                                        var status: String? = null
                                        if (code == 0) {
                                            if (data.getJSONObject("data").has("next_billing_timestamp")) {
                                                val outputFormat: DateFormat = SimpleDateFormat("E, dd MMM yyyy")
                                                outputFormat.timeZone = TimeZone.getTimeZone("GMT+5:00")
                                                val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
                                                val date = inputFormat.parse(data.getJSONObject("data").getString("next_billing_timestamp"))
                                                nextBilling = outputFormat.format(date)
                                            }
                                            status = data.getJSONObject("data").getString("subscription_status")
                                            val autoRen = data.getJSONObject("data").getBoolean("auto_renewal")
                                            mPrefs.setSubscribedPackageId(data.getString("subscribed_package_id"), slug)
                                            mPrefs.setStreamable(data.getJSONObject("data").has("is_allowed_to_stream") && data.getJSONObject("data").getBoolean("is_allowed_to_stream"), slug);

                                            //mPrefs.setAutoRenewal(autoRen, slug)
                                            mPrefs.setSubscriptionStatus(status, slug)
                                            mPrefs.setUserId(data.getJSONObject("data").getString("user_id"), slug)
                                            subStatus = status;
                                        } else {
                                            subStatus = PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED;
                                            mPrefs.setSubscriptionStatus(PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED, slug)
                                        }

                                        unSubUser(mPackagesList[position], nextBilling, subStatus, slug);
                                        notifyDataSetChanged();
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }

                                override fun onFailed(code: Int, reason: String?) {
                                    smallPb.setVisibility(View.GONE)
                                }
                            }).exec()
                    }
                    else {
                        // different package id - it means switch package
                        switchPackage(mPackagesList[position]);
                    }
                }
                else{
                    val intent = Intent(mContext, PaywallActivity::class.java);
                    intent.putExtra(PaywallActivity.ARG_PAYWALL_SLUG, PaywallGoonjFragment.SLUG)
                    intent.putExtra(PaywallActivity.ARG_PAYWALL_PACKAGE, mPackagesList[position])
                    mContext.startActivity(intent);
                    //(mContext as BaseActivity).finish()
                }
            }
            else if(slug == PaywallComedyFragment.SLUG){
                // comedy
                if(mPrefs.getSubscriptionStatus(slug).equals("billed")){
                    // unsubscribe
                    ComedyPaymentHelper(mContext).unsubscribe(mPackagesList[position].id, mPrefs.getUserId(slug)!!, object: ComedyPaymentHelper.UnsubscribedListener {
                        override fun onStatus(code: Int, status: String) {
                            Toaster.printToast(mContext, "Unsubscribed successfully");
                            notifyDataSetChanged();
                        }
                    });
                }else{
                    val intent = Intent(mContext, PaywallActivity::class.java);
                    intent.putExtra(PaywallActivity.ARG_PAYWALL_SLUG, PaywallComedyFragment.SLUG)
                    mContext.startActivity(intent);
                    //(mContext as BaseActivity).finish()
                }
            }
            else{
                // binjee
                if(mPrefs.getSubscriptionStatus(slug).equals("billed")){
                    // unsubscribe
                    BinjeePaymentHelper(mContext).unsubscribe(mPrefs.getMsisdn(slug)!!, object: BinjeePaymentHelper.UnsubscribedListener {
                        override fun onStatus(code: Int, status: String) {
                            Toaster.printToast(mContext, "Unsubscribed successfully");
                            notifyDataSetChanged();
                        }
                    });
                }else{
                    val intent = Intent(mContext, PaywallActivity::class.java);
                    intent.putExtra(PaywallActivity.ARG_PAYWALL_SLUG, PaywallBinjeeFragment.SLUG)
                    mContext.startActivity(intent);
                    //(mContext as BaseActivity).finish()
                }
            }
        })

        /*val subPackageId: String? = mPrefs.getSubscribedPackageId(slug)
        if (slug != PaywallComedyFragment.SLUG && subPackageId == mPackagesList[position].id && mPrefs.isOtpValidated()) {
            val postBody = ArrayList<Params>()
            postBody.add(Params("msisdn", mPrefs.getMsisdn(mPackagesList[position].slug)))
            postBody.add(Params("source", "app"))
            postBody.add(Params("package_id", mPackagesList[position].id))
            RestClient(mContext, Constants.PAYWALL_BASE_URL + Constants.Companion.EndPoints.CHECK_GOONJ_SUBSCRIPTION, RestClient.Companion.Method.POST, postBody, object : NetworkOperationListener {

                override fun onSuccess(response: String?) {
                    smallPb.visibility = View.GONE
                        try {
                            val data = JSONObject(response)
                            val code = data.getInt("code")
                            val subStatus: String;
                            var nextBilling: String? = null
                            var status: String? = null
                            if (code == 0) {
                                if (data.getJSONObject("data").has("next_billing_timestamp")) {
                                    val outputFormat: DateFormat = SimpleDateFormat("E, dd MMM yyyy")
                                    outputFormat.timeZone = TimeZone.getTimeZone("GMT+5:00")
                                    val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
                                    val date = inputFormat.parse(data.getJSONObject("data").getString("next_billing_timestamp"))
                                    nextBilling = outputFormat.format(date)
                                }
                                status = data.getJSONObject("data").getString("subscription_status")
                                val autoRen = data.getJSONObject("data").getBoolean("auto_renewal")
                                mPrefs.setSubscribedPackageId(data.getString("subscribed_package_id"), slug)
                                mPrefs.setStreamable(data.getJSONObject("data").has("is_allowed_to_stream") && data.getJSONObject("data").getBoolean("is_allowed_to_stream"),slug);

                                //mPrefs.setAutoRenewal(autoRen, slug)
                                mPrefs.setSubscriptionStatus(status, slug)
                                mPrefs.setUserId(data.getJSONObject("data").getString("user_id"), slug)
                                subStatus = status;
                            } else {
                                subStatus = PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED
                                mPrefs.setSubscriptionStatus(PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED, slug)
                            }
                            if ((subStatus === PaymentHelper.Companion.PaymentStatus.STATUS_GRACED || subStatus === PaymentHelper.Companion.PaymentStatus.STATUS_BILLED || subStatus === PaymentHelper.Companion.PaymentStatus.STATUS_TRIAL)) {
                                //subsStateImg.setImageResource(R.drawable.unfollow)
                                subsText.setText("Unsubscribe")
                                val finalNextBilling = nextBilling
                                val finalStatus = status
                                subStatusLayout.setOnClickListener {
                                    *//*unSubUser(
                                        mPackagesList[position],
                                        finalNextBilling,
                                        finalStatus,
                                        slug
                                    )*//*
                                }
                            } else {
                                // Show subscriber consent, as no need for OTP in case of HE
                                //subsStateImg.setImageResource(R.drawable.follow)
                                subsText.setText("Subscribe Now")
                                subStatusLayout.setOnClickListener {
                                    //switchPackage(mPackagesList[position])
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                override fun onFailed(code: Int, reason: String?) {
                    smallPb.setVisibility(View.GONE)
                }
            }).exec()
        }
        else {
            if (slug == PaywallComedyFragment.SLUG && mPrefs.getMsisdn(PaywallComedyFragment.SLUG) != null) {
                val map: MutableMap<String, String> = HashMap()
                map["phone"] = mPrefs.getMsisdn(PaywallComedyFragment.SLUG)!!
                RestClient(
                    mContext,
                    Constants.COMEDY_BASE_URL + Constants.Companion.EndPoints.CHECK_COMEDY_SUBSCRIPTION,
                    RestClient.Companion.Method.POST,
                    null, object: NetworkOperationListener {
                        override fun onSuccess(response: String?) {
                            smallPb.setVisibility(View.GONE)
                            Logger.println("checkStatus - onSuccess: $response")
                            try {
                                val rootObj = JSONObject(response)
                                if (rootObj.has("message") && rootObj.getString("message") == "User is subscribed") {
                                    //subsStateImg.setImageResource(R.drawable.unfollow)
                                    subsText.setText("Unsubscribe")
                                    val finalStatus = "billed"
                                    subStatusLayout.setOnClickListener {
                                        *//*unSubUser(
                                            mPackagesList[position],
                                            null,
                                            finalStatus,
                                            slug
                                        )*//*
                                    }
                                } else {
                                    //subsStateImg.setImageResource(R.drawable.follow)
                                    subsText.setText("Subscribe Now")
                                    subStatusLayout.setOnClickListener {
                                        //switchPackage(mPackagesList[position])
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onFailed(code: Int, reason: String?) {
                            smallPb.setVisibility(View.GONE)
                        }
                    }).execComedy()
            }
            else {
                //subsStateImg.setImageResource(R.drawable.follow)
                subsText.setText("Subscribe Now")
                subStatusLayout.setOnClickListener { view12: View? ->
                    //switchPackage(mPackagesList[position])
                }
                smallPb.setVisibility(View.GONE)
            }
        }*/

        return view;
    }

    private fun unSubUser(mPackage: PackageModel, nextBilling: String?, billingStatus: String?, slug: String) {
        var message = "Are you sure you want to sign-out from " + mPackage.name + "?"
        if (nextBilling != null) {
            message =
                "Your " + (if (billingStatus == "trial") "free tiral" else "subscription") + " is valid till " + nextBilling + ". " + message
        }
        val dialog: AlertDialog = DialogManager().getUnSubDialog(mContext, message)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mContext.getResources().getColor(R.color.colorRed))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { view12: View? ->
            dialog.findViewById<View>(R.id.unsubLayout).visibility = View.GONE
            dialog.findViewById<View>(R.id.waitingLayout).visibility = View.VISIBLE
            if (slug == PaywallComedyFragment.SLUG) {
                ComedyPaymentHelper(mContext).unsubscribe(mPackage.id, mPrefs.getUserId(slug)!!, object: ComedyPaymentHelper.UnsubscribedListener {
                    override fun onStatus(code: Int, status: String) {
                        Toaster.printToast(mContext, "Signed-out!")
                        //mPrefs.setComedyAutoRenewal(false)
                        notifyDataSetChanged()
                        dialog.dismiss()
                    }
                });
            }else if (slug == PaywallBinjeeFragment.SLUG) {
                BinjeePaymentHelper(mContext).unsubscribe(mPrefs.getMsisdn(slug)!!, object: BinjeePaymentHelper.UnsubscribedListener {
                    override fun onStatus(code: Int, status: String) {
                        Toaster.printToast(mContext, "Signed-out!")
                        //mPrefs.setComedyAutoRenewal(false)
                        notifyDataSetChanged()
                        dialog.dismiss()
                    }
                });
            } else {
                val paramsArrayList = ArrayList<Params>()
                paramsArrayList.add(Params("msisdn", mPrefs.getMsisdn(mPackage.slug)))
                paramsArrayList.add(Params("source", "app"))
                paramsArrayList.add(Params("package_id", mPackage.id))
                RestClient(
                    mContext,
                    Constants.PAYWALL_BASE_URL + Constants.Companion.EndPoints.UN_SUBSCRIBE,
                    RestClient.Companion.Method.POST,
                    paramsArrayList,
                    object : NetworkOperationListener {
                        override fun onSuccess(response: String?) {
                            try {
                                val root = JSONObject(response)
                                if (root.getInt("code") == 0) {
                                    mPrefs.setSubscriptionStatus(PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED, PaywallGoonjFragment.SLUG);
                                    Toaster.printToast(mContext, "Signed-out")
                                    notifyDataSetChanged();
                                } else {
                                    Toaster.printToast(mContext, "Signin-out Failed")
                                }
                                if (dialog.isShowing) dialog.dismiss()
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onFailed(code: Int, reason: String?) {
                            Toaster.printToast(mContext, "Failed to process request, possible cause: $reason")
                            if (dialog.isShowing) dialog.dismiss()
                        }
                    }).exec()
            }
        }
    }

    private fun switchPackage(mNewPackge: PackageModel) {
        val dialog: AlertDialog = DialogManager().getSubscriptionDialog(mContext);
        dialog.show()
        val subscriptionLayout = dialog.findViewById<LinearLayout>(R.id.subscriptionLayout)
        val waitingLayout = dialog.findViewById<LinearLayout>(R.id.validationLayout)
        val textView = dialog.findViewById<TextView>(R.id.subscriptionScreenMessage)
        val numberVerifiedTxt = dialog.findViewById<TextView>(R.id.numberVerifiedTxt)
        numberVerifiedTxt.text = "Change Package"
        waitingLayout.visibility = View.GONE
        val text = "You will be charged " + mNewPackge.desc + " for this service until you sign-out"
        textView.text = text
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mContext.getResources().getColor(R.color.light_blue))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { v: View? ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
            waitingLayout.visibility = View.VISIBLE
            subscriptionLayout.visibility = View.GONE
            val paramsArrayList = arrayListOf<Params>()
            paramsArrayList.add(Params("msisdn", mPrefs.getMsisdn(slug)));
            paramsArrayList.add(Params("package_id", mNewPackge.id))
            paramsArrayList.add(Params("source", "app"))
            RestClient(
                mContext,
                Constants.PAYWALL_BASE_URL + Constants.Companion.EndPoints.SUBSCRIBE,
                RestClient.Companion.Method.POST, paramsArrayList, object : NetworkOperationListener {
                    override fun onSuccess(response: String?) {
                        try {
                            val rootObj = JSONObject(response)
                            if (rootObj.has("package_id")) {
                                mPrefs.setSubscribedPackageId(rootObj.getString("package_id"), slug)
                            }

                            if (rootObj.getInt("code") == 0 || rootObj.getInt("code") == 9) {
                                // success
                                mPrefs.setSubscribedPackageId(
                                    mNewPackge.id,
                                    slug
                                )
                                Toaster.printToast(
                                    mContext,
                                    rootObj.getString("message")
                                )
                                if (dialog.isShowing) {
                                    dialog.dismiss()
                                }
                                notifyDataSetChanged();
                            } else {
                                Toaster.printToast(mContext, rootObj.getString("message"))
                                if (dialog.isShowing) {
                                    dialog.dismiss()
                                }
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            notifyDataSetChanged()
                        }
                    }

                    override fun onFailed(code: Int, reason: String?) {
                        Logger.println("Error: $reason")
                    }
                }).exec()
        }
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener { v: View? -> if (dialog.isShowing) dialog.dismiss() }
    }
}