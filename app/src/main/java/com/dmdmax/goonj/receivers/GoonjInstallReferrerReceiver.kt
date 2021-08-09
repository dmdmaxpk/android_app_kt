package com.dmdmax.goonj.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dmdmax.goonj.base.BaseApplication
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import java.lang.Exception
import java.net.URLDecoder


class GoonjInstallReferrerReceiver: BroadcastReceiver() {

    val KEY_UTM_SOURCE = "utm_source"
    val KEY_UTM_MEDIUM = "utm_medium"
    val KEY_UTM_CAMPAIGN = "utm_campaign"

    override fun onReceive(context: Context?, intent: Intent) {
        try {
            val referrer = URLDecoder.decode(intent.getStringExtra("referrer"), "UTF-8")
            if (referrer != null && referrer != "") {
                val tracker: Tracker? = BaseApplication.getInstance().getGoogleAnalyticsTracker()
                tracker?.setScreenName(GoonjInstallReferrerReceiver::class.java.toString())
                tracker?.send(HitBuilders.ScreenViewBuilder().setCampaignParamsFromUrl(referrer.replace("%20", "-")).build())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getData(key: String, allData: Array<String>): String? {
        for (selected in allData) if (selected.contains(key)) {
            return selected.split("=").toTypedArray()[1].replace("%20".toRegex(), " ")
        }
        return ""
    }

}