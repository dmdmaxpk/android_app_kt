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
import com.dmdmax.goonj.screens.activities.PlayerActivity
import com.dmdmax.goonj.screens.activities.SigninActivity
import com.dmdmax.goonj.screens.views.PaywallBillingView
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Toaster
import org.json.JSONArray
import org.json.JSONObject

class PaywallGoonjFragment: BaseFragment(), View.OnClickListener, PaywallBillingView {

    private lateinit var mPaywall: Paywall;
    private var isHEHappen = false;

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
        mTelenorNumber.visibility = View.INVISIBLE;

        mEasypaisaNumber = view.findViewById(R.id.ep_number)
        mEasypaisaNumber.setOnClickListener(this);
        //mEasypaisaNumber.visibility = View.INVISIBLE;

        mPaywall = requireArguments().getSerializable(ARGS_TAB) as Paywall

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
        /*RestClient(context!!, "http://he.goonj.pk/he", RestClient.Companion.Method.GET, null, object: NetworkOperationListener {
            override fun onSuccess(response: String?) {
                val rootObj = JSONObject(response);
                Logger.println("HE: $response");
                if(rootObj.has("msisdn") && !rootObj.getString("msisdn").equals("null")) {
                    mPrefs.setMsisdn(rootObj.getString("msisdn"), SLUG);
                    mPrefs.setAccessToken(rootObj.getString("access_token"));
                    mPrefs.setRefreshToken(rootObj.getString("refresh_token"))

                    isHEHappen = true;
                }



            }

            override fun onFailed(code: Int, reason: String?) {
                TODO("Not yet implemented")
            }
        }).exec()*/


        RestClient(requireContext(), Constants.API_BASE_URL + Constants.Companion.EndPoints.PACKAGE, RestClient.Companion.Method.GET, null, object: NetworkOperationListener {
            override fun onSuccess(response: String?) {
                val list = arrayListOf<PackageModel>()
                Logger.println("fetchPackages: $response");
                val rootObj = JSONArray(response);
                for(i in 0 until rootObj.length()) {
                    list.add(
                        PackageModel(
                            rootObj.getJSONObject(i).getString("_id"),
                            rootObj.getJSONObject(i).getString("package_name"),
                            rootObj.getJSONObject(i).getString("price_point_pkr"),
                            rootObj.getJSONObject(i).getString("package_desc"),
                            rootObj.getJSONObject(i).getString("slug"),
                            rootObj.getJSONObject(i).getBoolean("default"),
                            rootObj.getJSONObject(i).getString("pid")
                        )
                    )
                }

                mProgressBar.visibility = View.GONE;
                mTelenorNumber.visibility = View.VISIBLE;
                //mEasypaisaNumber.visibility = View.VISIBLE;

                try{
                    if(mPaywall.mSelectedPackage != null){
                        mDefaultPackage = list.find { packageModel -> packageModel.id == mPaywall.mSelectedPackage?.id }!!
                    }else{
                        mDefaultPackage = list.find { packageModel -> packageModel.default }!!
                    }
                }catch (e: Exception) {
                    Logger.println("Exception: " + e.message);
                }

                Logger.println("Default Package: " + mDefaultPackage.name + " - " + mDefaultPackage.price);
                mPackageName.text = mDefaultPackage.name;
                mPackagePrice.text = mDefaultPackage.text;
                mPrefs.setSubscribedPackageId(mDefaultPackage.id, SLUG);
                mPrefs.setServiceId(mDefaultPackage.serviceId)
            }

            override fun onFailed(code: Int, reason: String?) {
                TODO("Not yet implemented")
            }
        }).exec();
    }

    override fun processBilling(source: String) {
        try{
            if(isHEHappen){
                mTelenorNumber.visibility = View.INVISIBLE;
                //mEasypaisaNumber.visibility = View.INVISIBLE;
                mProgressBar.visibility = View.VISIBLE;

                val helper = PaymentHelper(requireContext(), "telenor");
                helper.subscribeNow(mPrefs.getMsisdn(SLUG), mDefaultPackage, "telenor", null, object: PaymentHelper.SubscribeNowListener {
                    override fun onSubscriptionResponse(billed: Boolean, response: String?, allowedToStream: Boolean) {
                        Logger.println("subscribeNow: $response");

                        if (billed && allowedToStream) {
                            if (PlayerActivity.ARGS_CHANNEL != null || PlayerActivity.ARGS_VIDEO != null) {
                                getCompositionRoot().getViewFactory().toPlayerScreen(null, null);
                            }
                            //Toaster.printToast(requireContext(), "Subscribed successfully");
                            (requireContext() as BaseActivity).finish()
                        } else {
                            Toaster.printToast(
                                requireContext(),
                                "Failed to subscribe, please check your balance and try again."
                            )

                            (requireContext() as BaseActivity).finish();
                        }
                    }
                })
            }else{
                EventManager.getInstance(requireContext()).fireEvent(EventManager.Events.GOONJ_PAYWALL_PAY_CLICK);
                val intent = Intent(context, SigninActivity::class.java);
                intent.putExtra(ARG_SUBSCRIPTION_SOURCE, SLUG);
                intent.putExtra(ARG_PAYMENT_SOURCE, source);
                intent.putExtra(ARGS_DEFAULT_PACKAGE, mDefaultPackage);
                startActivity(intent);
                (requireContext() as BaseActivity).finish()
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}