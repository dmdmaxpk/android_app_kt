package com.dmdmax.goonj.screens.implements

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.dmdmax.goonj.R
import com.dmdmax.goonj.adapters.PackageListAdapter
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.models.PackageModel
import com.dmdmax.goonj.models.Paywall
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.screens.activities.WebViewActivity
import com.dmdmax.goonj.screens.fragments.paywall.PaywallBinjeeFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.views.SubscriptionStatusView
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.WrapContentListView
import org.json.JSONObject

class SubscriptionStatusViewImpl: BaseObservableView<SubscriptionStatusView.Listener>, SubscriptionStatusView {

    private lateinit var mBackArrow: ImageView;
    private lateinit var paywallDetails: LinearLayout;
    private lateinit var packagesPb: ProgressBar;

    private lateinit var mContactUsLayout: FrameLayout;
    private lateinit var mViewTermsConditions: FrameLayout;
    private lateinit var mViewPrivacyPolicy: FrameLayout;

    private lateinit var mAppVersion: TextView;
    private lateinit var mContactUsMobileNumber: TextView;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.activity_subscription, parent, false));
    }

    override fun initialize() {
        mBackArrow = findViewById(R.id.back_arrow);
        paywallDetails = findViewById(R.id.paywallDetails);
        packagesPb = findViewById(R.id.packagesPb);

        mContactUsLayout = findViewById(R.id.contact_us_layout);
        mViewPrivacyPolicy = findViewById(R.id.view_privacy_policy);
        mViewTermsConditions = findViewById(R.id.view_terms_conditions);
        mAppVersion = findViewById(R.id.app_version);
        mContactUsMobileNumber = findViewById(R.id.contact_us_mobile_number);

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
            intent.putExtra("page", "terms")
            getContext().startActivity(intent);
        }

        mViewPrivacyPolicy.setOnClickListener {
            val intent = Intent(getContext(), WebViewActivity::class.java)
            intent.putExtra("page", "privacy-policy")
            getContext().startActivity(intent);
        }

        mBackArrow.setOnClickListener {
            (getContext() as BaseActivity).finish();
        }

        displayPaywallDetails();
    }

    private fun displayPaywallDetails() {
        paywallDetails.removeAllViews()
        paywallDetails.addView(packagesPb)
        packagesPb.setVisibility(View.VISIBLE)
        RestClient(getContext(), Constants.PAYWALL_BASE_URL + Constants.Companion.EndPoints.PAYWALL, RestClient.Companion.Method.GET, null, object : NetworkOperationListener {
                override fun onSuccess(response: String?) {
                    try {
                        val paywalls = arrayListOf<Paywall>();
                        val data = JSONObject(response).getJSONArray("data")
                        for (i in 0 until data.length()) {
                            val p = Paywall()
                            p.id = data.getJSONObject(i).getString("_id")
                            p.active = data.getJSONObject(i).getBoolean("active")
                            //p.name = data.getJSONObject(i).getString("paywall_name")
                            p.name = "Live"
                            p.desc = data.getJSONObject(i).getString("paywall_desc")
                            p.slug = data.getJSONObject(i).getString("slug")

                            val mPackages = arrayListOf<PackageModel>();
                            val packages = data.getJSONObject(i).getJSONArray("packages")
                            for (j in 0 until packages.length()) {
                                val mPackage = PackageModel();
                                mPackage.id = packages.getJSONObject(j).getString("_id");
                                mPackage.name = packages.getJSONObject(j).getString("package_name")
                                mPackage.desc = packages.getJSONObject(j).getString("package_desc")
                                mPackage.price = packages.getJSONObject(j).getString("display_price_point");
                                mPackage.paywallId = packages.getJSONObject(j).getString("paywall_id");
                                mPackage.slug = p.slug;

                                mPackages.add(mPackage)
                            }
                            p.packages = mPackages;
                            paywalls.add(p)
                        }

                        for (i in paywalls.indices) {
                            val view: View = LayoutInflater.from(getContext()).inflate(R.layout.package_unsub_details_layout, null, false)
                            val paywall = paywalls[i]
                            val paywallName = view.findViewById<TextView>(R.id.paywallName)
                            val listView: WrapContentListView = view.findViewById(R.id.packageDetails)
                            paywallName.text = paywall.name
                            val packages: List<PackageModel> = paywall.packages;
                            val mAdapter = PackageListAdapter(packages, PaywallGoonjFragment.SLUG, getContext());
                            listView.adapter = mAdapter
                            paywallDetails.addView(view)
                        }

                        RestClient(getContext(), Constants.COMEDY_BASE_URL + Constants.Companion.EndPoints.GET_COMEDY_PACKAGES, RestClient.Companion.Method.GET, null, object : NetworkOperationListener {
                                override fun onSuccess(response: String?) {
                                    try {
                                        val paywalls = arrayListOf<Paywall>()
                                        val data = JSONObject(response)
                                        for (i in 0 until data.length()) {
                                            val p = Paywall()
                                            p.id = "123"
                                            p.active = true;
                                            p.name = "Comedy"
                                            p.desc = "No Desc"
                                            p.slug = PaywallComedyFragment.SLUG
                                            val mPackages = arrayListOf<PackageModel>()
                                            val packages = data.getJSONArray("package")

                                            for (j in 0 until packages.length()) {
                                                val mPackage = PackageModel()
                                                mPackage.id = packages.getJSONObject(i).getString("plan_id");
                                                mPackage.name = packages.getJSONObject(j).getString("name");
                                                mPackage.desc = (packages.getJSONObject(j).getString("name"))
                                                mPackage.price = packages.getJSONObject(j).getString("price")
                                                mPackage.paywallId = p.id;
                                                mPackage.slug = p.slug;
                                                mPackages.add(mPackage)
                                            }
                                            p.packages = mPackages;
                                            paywalls.add(p)
                                        }
                                        packagesPb.visibility = View.GONE

                                        for (i in paywalls.indices) {
                                            val view: View = LayoutInflater.from(getContext()).inflate(R.layout.package_unsub_details_layout, null, false)
                                            val paywall = paywalls[i]
                                            val paywallName = view.findViewById<TextView>(R.id.paywallName)
                                            val listView: WrapContentListView = view.findViewById(R.id.packageDetails)
                                            paywallName.text = paywall.name
                                            val packages: List<PackageModel> = paywall.packages;
                                            val mAdapter = PackageListAdapter(packages, PaywallComedyFragment.SLUG, getContext());
                                            listView.adapter = mAdapter
                                            paywallDetails.addView(view)
                                        }

                                        // Binjee packages
                                        val p = Paywall()
                                        p.id = "123"
                                        p.active = true;
                                        p.name = "Binjee"
                                        p.desc = "No Desc"
                                        p.slug = PaywallBinjeeFragment.SLUG
                                        val mPackages = arrayListOf<PackageModel>()

                                        val mPackage = PackageModel()
                                        mPackage.id = "no-id"
                                        mPackage.name = PaywallBinjeeFragment.BINJEE_PACKAGE;
                                        mPackage.desc = "Binjee daily package"
                                        mPackage.slug = p.slug;
                                        mPackages.add(mPackage)
                                        p.packages = mPackages;

                                        val view: View = LayoutInflater.from(getContext()).inflate(R.layout.package_unsub_details_layout, null, false)
                                        val paywallName = view.findViewById<TextView>(R.id.paywallName)
                                        val listView: WrapContentListView = view.findViewById(R.id.packageDetails)
                                        paywallName.text = p.name
                                        val packages: List<PackageModel> = p.packages;
                                        val mAdapter = PackageListAdapter(packages, PaywallBinjeeFragment.SLUG, getContext());
                                        listView.adapter = mAdapter
                                        paywallDetails.addView(view)

                                    } catch (e: java.lang.Exception) {
                                        e.printStackTrace()
                                    }
                                }

                                override fun onFailed(code: Int, reason: String?) {
                                    packagesPb.setVisibility(View.GONE)
                                }
                            }).execComedy()
                    }
                    catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailed(code: Int, reason: String?) {
                    packagesPb.setVisibility(View.GONE)
                }
            }).exec();
    }
}