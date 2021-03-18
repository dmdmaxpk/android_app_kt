package com.dmdmax.goonj.screens.implements

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import com.dmdmax.goonj.R
import com.dmdmax.goonj.adapters.PackageListAdapter
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.models.PaywallPackage
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.screens.activities.WebViewActivity
import com.dmdmax.goonj.screens.views.SubscriptionStatusView
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.WrapContentListView
import org.json.JSONObject
import kotlin.collections.ArrayList

class SubscriptionStatusViewImpl: BaseObservableView<SubscriptionStatusView.Listener>, SubscriptionStatusView {

    private lateinit var mBackArrow: ImageView;
    private lateinit var mLiveLayout: LinearLayout;
    private lateinit var mComedyLayout: LinearLayout;

    private lateinit var mContactUsLayout: FrameLayout;
    private lateinit var mViewTermsConditions: FrameLayout;
    private lateinit var mViewPrivacyPolicy: FrameLayout;

    private lateinit var mAppVersion: TextView;
    private lateinit var mContactUsMobileNumber: TextView;

    private lateinit var mLivePaywallList: WrapContentListView;
    private lateinit var mComedyPaywallList: WrapContentListView;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.activity_subscription, parent, false));
    }

    override fun initialize() {
        mBackArrow = findViewById(R.id.back_arrow);
        mLiveLayout = findViewById(R.id.live_paywall_layout);
        mComedyLayout = findViewById(R.id.comedy_paywall_layout);

        mContactUsLayout = findViewById(R.id.contact_us_layout);
        mViewPrivacyPolicy = findViewById(R.id.view_privacy_policy);
        mViewTermsConditions = findViewById(R.id.view_terms_conditions);
        mAppVersion = findViewById(R.id.app_version);
        mContactUsMobileNumber = findViewById(R.id.contact_us_mobile_number);

        mLivePaywallList = findViewById(R.id.live_paywall_list);
        mComedyPaywallList = findViewById(R.id.comedy_paywall_list);


        try {
            val pInfo: PackageInfo = getContext().packageManager.getPackageInfo(
                getContext().packageName,
                0
            )
            mAppVersion.text = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        mContactUsMobileNumber.text = Constants.CONTACT_US_NUMBER;
        mContactUsLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + Constants.CONTACT_US_NUMBER)
            getContext().startActivity(intent)
        }

        mViewTermsConditions.setOnClickListener {
            val intent = Intent(getContext(), WebViewActivity::class.java)
            intent.putExtra("page", Constants.TERMS_URL)
            getContext().startActivity(intent);
        }

        mViewPrivacyPolicy.setOnClickListener {
            val intent = Intent(getContext(), WebViewActivity::class.java)
            intent.putExtra("page", Constants.PRIVACY_POLICY_URL)
            getContext().startActivity(intent);
        }

        mBackArrow.setOnClickListener {
            (getContext() as BaseActivity).finish();
        }

        displayLivePaywallDetails();
        displayComedyPaywallDetails();
    }

    private fun displayLivePaywallDetails(){
        RestClient(getContext(), Constants.PAYWALL_BASE_URL + Constants.Companion.EndPoints.PAYWALL, RestClient.Companion.Method.GET, null, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                try {
                    val data = JSONObject(response).getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val mPackages: ArrayList<PaywallPackage> = arrayListOf();
                        val packages = data.getJSONObject(i).getJSONArray("packages")
                        for (j in 0 until packages.length()) {
                            val mPackage = PaywallPackage()
                            mPackage.setId(packages.getJSONObject(j).getString("_id"))
                            mPackage.setName(packages.getJSONObject(j).getString("package_name"))
                            mPackage.setDesc(packages.getJSONObject(j).getString("package_desc"))
                            mPackage.setPricePoint(packages.getJSONObject(j).getString("display_price_point"))
                            mPackage.setPaywallId(packages.getJSONObject(j).getString("paywall_id"))
                            mPackage.setSlug(packages.getJSONObject(j).getString("slug"))
                            mPackages.add(mPackage)
                        }
                        mLivePaywallList.adapter = PackageListAdapter(mPackages, data.getJSONObject(i).getString("paywall_name"), getContext());

                    }
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailed(code: Int, reason: String?) {
                TODO("Not yet implemented")
            }
        }).exec();
    }

    private fun displayComedyPaywallDetails(){
        RestClient(getContext(), Constants.PAYWALL_BASE_URL + Constants.Companion.EndPoints.PAYWALL, RestClient.Companion.Method.GET, null, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                try {
                    val data = JSONObject(response).getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val mPackages: ArrayList<PaywallPackage> = arrayListOf();
                        val packages = data.getJSONObject(i).getJSONArray("packages")
                        for (j in 0 until packages.length()) {
                            val mPackage = PaywallPackage()
                            mPackage.setId(packages.getJSONObject(j).getString("_id"))
                            mPackage.setName(packages.getJSONObject(j).getString("package_name"))
                            mPackage.setDesc(packages.getJSONObject(j).getString("package_desc"))
                            mPackage.setPricePoint(packages.getJSONObject(j).getString("display_price_point"))
                            mPackage.setPaywallId(packages.getJSONObject(j).getString("paywall_id"))
                            mPackage.setSlug(packages.getJSONObject(j).getString("slug"))
                            mPackages.add(mPackage)
                        }
                        mComedyPaywallList.adapter = PackageListAdapter(mPackages, data.getJSONObject(i).getString("paywall_name"), getContext());

                    }
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailed(code: Int, reason: String?) {
                TODO("Not yet implemented")
            }
        }).exec();
    }
}