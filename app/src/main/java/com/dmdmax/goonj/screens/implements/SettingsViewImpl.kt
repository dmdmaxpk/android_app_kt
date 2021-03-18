package com.dmdmax.goonj.screens.implements

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.dmdmax.goonj.R
import com.dmdmax.goonj.adapters.PackageListAdapter
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.models.PaywallPackage
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.screens.activities.MyProfileActivity
import com.dmdmax.goonj.screens.activities.SubscriptionActivity
import com.dmdmax.goonj.screens.activities.WebViewActivity
import com.dmdmax.goonj.screens.views.SettingsView
import com.dmdmax.goonj.screens.views.SubscriptionStatusView
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.WrapContentListView
import org.json.JSONObject
import kotlin.collections.ArrayList

class SettingsViewImpl: BaseObservableView<SettingsView.Listener>, SettingsView, View.OnClickListener {

    private lateinit var mMyProfile: LinearLayout;
    private lateinit var mMySubscriptions: LinearLayout;

    private lateinit var mUsername: TextView;

    constructor(inflater: LayoutInflater, parent: ViewGroup) {
        setRootView(inflater.inflate(R.layout.fragment_settings, parent, false));
    }

    override fun initialize() {
        mMyProfile = findViewById(R.id.my_profile);
        mMySubscriptions = findViewById(R.id.my_subscriptions);
        mUsername = findViewById(R.id.user_name);

        mMyProfile.setOnClickListener(this);
        mMySubscriptions.setOnClickListener(this)
    }

    override fun setUsername() {
        val mGoonjPrefs = GoonjPrefs(getContext());
        if(!mGoonjPrefs.getUsername()!!.isEmpty()){
            mUsername.text = mGoonjPrefs.getUsername();
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            mMyProfile.id -> {
                ContextCompat.startActivity(getContext(), Intent(getContext(), MyProfileActivity::class.java), null);
            }

            mMySubscriptions.id -> {
                ContextCompat.startActivity(getContext(), Intent(getContext(), SubscriptionActivity::class.java), null);
            }
        }
    }
}