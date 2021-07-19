package com.dmdmax.goonj.screens.fragments.paywall

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.base.BaseFragment
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.models.PackageModel
import com.dmdmax.goonj.models.Paywall
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.screens.activities.SigninActivity
import com.dmdmax.goonj.screens.views.PaywallBillingView
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import org.json.JSONArray

class PaywallGoonjFragment: BaseFragment(), View.OnClickListener, PaywallBillingView {

    private lateinit var mPaywall: Paywall;

    companion object {
        val SLUG = "live";
        val ARGS_TAB = "tab";
        val ARGS_DEFAULT_PACKAGE = "default_package";
        val ARG_PAYMENT_SOURCE = "payment_source";
        val ARG_SUBSCRIPTION_SOURCE = "subscription_source";

        fun newInstance(args: Bundle?): PaywallGoonjFragment {
            val fragment =  PaywallGoonjFragment();
            if (args != null) {
                fragment.arguments = args;
            }
            return fragment;
        }
    }

    private lateinit var mPackageName: TextView;
    private lateinit var mPackagePrice: TextView;

    private lateinit var mTelenorNumber: LinearLayout;
    private lateinit var mEasypaisaNumber: LinearLayout;

    private lateinit var mProgressBar: ProgressBar;
    private lateinit var mMainLayout: ConstraintLayout;

    private lateinit var mPrefs: GoonjPrefs;

    private lateinit var mDefaultPackage: PackageModel;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.paywall_goonj_fragment, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        mPackageName = view.findViewById(R.id.package_name);
        mPackagePrice = view.findViewById(R.id.package_price);

        mProgressBar = view.findViewById(R.id.progress_bar)
        mMainLayout = view.findViewById(R.id.main_layout);

        mPrefs = GoonjPrefs(context);

        mTelenorNumber = view.findViewById(R.id.telenor_number);
        mTelenorNumber.setOnClickListener(this)

        mEasypaisaNumber = view.findViewById(R.id.ep_number)
        mEasypaisaNumber.setOnClickListener(this);

        mPaywall = arguments!!.getSerializable(ARGS_TAB) as Paywall

        fetchPackages();
    }

    override fun onClick(v: View?) {
        when(v?.id){
            mEasypaisaNumber.id -> {
                processBilling(PaymentHelper.PAYMENT_EASYPAISA);
            }

            mTelenorNumber.id -> {
                processBilling(PaymentHelper.PAYMENT_TELENOR);
            }
        }
    }

    override fun fetchPackages() {
        RestClient(context!!, Constants.API_BASE_URL + Constants.Companion.EndPoints.PACKAGE, RestClient.Companion.Method.GET, null, object: NetworkOperationListener {
            override fun onSuccess(response: String?) {
                val list = arrayListOf<PackageModel>()
                Logger.println("fetchPackages: $response");
                val rootObj = JSONArray(response);
                for(i in 0 until rootObj.length()){
                    list.add(PackageModel(
                            rootObj.getJSONObject(i).getString("_id"),
                            rootObj.getJSONObject(i).getString("package_name"),
                            rootObj.getJSONObject(i).getString("price_point_pkr"),
                            rootObj.getJSONObject(i).getString("package_desc"),
                            rootObj.getJSONObject(i).getString("slug"),
                            rootObj.getJSONObject(i).getBoolean("default")
                    ))
                }

                mProgressBar.visibility = View.GONE;
                mMainLayout.visibility = View.VISIBLE;

                if(mPaywall.mSelectedPackage != null){
                    mDefaultPackage = list.find { packageModel -> packageModel.id == mPaywall.mSelectedPackage?.id }!!
                }else{
                    mDefaultPackage = list.find { packageModel -> packageModel.default }!!
                }


                mPackageName.text = mDefaultPackage.name;
                mPackagePrice.text = mDefaultPackage.text;
            }

            override fun onFailed(code: Int, reason: String?) {
                TODO("Not yet implemented")
            }
        }).exec();
    }

    override fun processBilling(source: String) {
        try{
            EventManager.getInstance(context!!).fireEvent(EventManager.Events.GOONJ_PAYWALL_PAY_CLICK);
            val intent = Intent(context, SigninActivity::class.java);
            intent.putExtra(ARG_SUBSCRIPTION_SOURCE, SLUG);
            intent.putExtra(ARG_PAYMENT_SOURCE, source);
            intent.putExtra(ARGS_DEFAULT_PACKAGE, mDefaultPackage);
            startActivity(intent);
            (context as BaseActivity).finish()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}