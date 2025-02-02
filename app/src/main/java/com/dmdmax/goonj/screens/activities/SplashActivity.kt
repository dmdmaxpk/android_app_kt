package com.dmdmax.goonj.screens.activities

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.controllers.VideoDownloadController
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.models.Params
import com.dmdmax.goonj.models.Video
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.views.SplashView
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class SplashActivity : BaseActivity(), SplashView.Listener {

    private lateinit var mView: SplashView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        mView = getCompositionRoot().getViewFactory().getSplashViewImpl(null);
        setContentView(mView.getRootView());

        if(!Utility.isConnectedToInternet(this)) {
            startActivity(Intent(this@SplashActivity, OfflineVideosActivity::class.java));
            finish();
        }else{
            mView.getRemoteConfigs();
            EventManager.getInstance(this).fireEvent("Splash${EventManager.Events.VIEW}");
        }
    }

    override fun onStart() {
        super.onStart();
        mView.registerListener(this);
    }

    override fun onStop() {
        super.onStop();
        mView.unregisterListener(this);
    }

    override fun onCompleted() {
        checkUrl()
    }

    private fun checkUrl() {
        //Logger.println("checkUrl - SplashActivity");

        val intent = intent
        val bundle: Bundle? = intent?.extras;

        if(bundle != null && bundle.containsKey("notification_id")){
            // Notification id found, send callback
            RestClient(this, Constants.API_BASE_URL + "notification/update?id=${bundle.getString("notification_id")}&user_id=${GoonjPrefs(this).getUserId(PaywallGoonjFragment.SLUG)}", RestClient.Companion.Method.PUT, null, object: NetworkOperationListener{
                override fun onSuccess(response: String?) {
                    Logger.println("checkUrl - notification/update - onSuccess: $response")
                }

                override fun onFailed(code: Int, reason: String?) {
                    Logger.println("checkUrl - notification/update - onFailed: $reason")
                }
            }).exec();
        }

        val data = intent.data
        if (data != null) {
            if (data.toString().contains("unsubscribe?")) {
                //https://goonj.pk/unsubscribe?proxy=5B4xdvAiX6ZJ
                //https://goonj.pk/unsubscribe?proxy=5B4xdvAiX6ZJ&pkg_id=QDfC

                val userId = data.toString().split('?')[1].split("&")[0].split("=")[1];
                val paramsArrayList = ArrayList<Params>()
                paramsArrayList.add(Params("user_id", userId))
                paramsArrayList.add(Params("source", "app"))


                RestClient(this@SplashActivity, Constants.API_BASE_URL + Constants.Companion.EndPoints.UN_SUBSCRIBE, RestClient.Companion.Method.POST, paramsArrayList,
                    object : NetworkOperationListener {
                        override fun onSuccess(response: String?) {
                            try {
                                val root = JSONObject(response)
                                if (root.getInt("code") == 0) {
                                    GoonjPrefs(this@SplashActivity).setSubscriptionStatus(PaymentHelper.Companion.PaymentStatus.NOT_SUBSCRIBED, PaywallGoonjFragment.SLUG);
                                    Toaster.printToast(this@SplashActivity, "Sign out success!");
                                } else {
                                    Toaster.printToast(this@SplashActivity, "Sign out Failed")
                                }

                                startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
                                finish()
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace();
                                Toaster.printToast(this@SplashActivity, "Exception occurred while signing out")

                                startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
                                finish()
                            }
                        }

                        override fun onFailed(code: Int, reason: String?) {
                            Toaster.printToast(this@SplashActivity, "Failed to process request, possible cause: $reason")

                            startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
                            finish()
                        }
                }).exec()
            }
            else if (data.toString().contains("/subscribe")) {

                val i = Intent(this@SplashActivity, WelcomeActivity::class.java)
                i.putExtra("action", DeepLinkingManager.Mapper.OPEN_LIVE_BOTTOM_MENU)
                startActivity(i)
                finish()
            }
            else if (data.toString().contains("/news")) {
                val i = Intent(this@SplashActivity, WelcomeActivity::class.java)
                var tabIndex = 0
                val link = data.toString()
                val arr = link.split("/".toRegex()).toTypedArray()
                if (arr.size > 0) {
                    try {
                        tabIndex = arr[arr.size - 1].toInt()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                i.putExtra("tabIndex", tabIndex)
                i.putExtra("action", DeepLinkingManager.Mapper.OPEN_NEWS)
                startActivity(i)
                finish()
            }
            else {
                val videoId = Utility.getVideoIdForVOD(data.toString())
                if (videoId!!.isEmpty()) {
                    // Means no VOD, check for others
                    val mapper: String = DeepLinkingManager.getMappedValue(data.toString())
                    if (mapper == DeepLinkingManager.Mapper.OPEN_APP) {
                        // Default, open app
                        startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
                        finish()
                    } else if (mapper == DeepLinkingManager.Mapper.OPEN_LIVE_BOTTOM_MENU) {
                        val i = Intent(this@SplashActivity, WelcomeActivity::class.java)
                        i.putExtra("action", mapper)
                        startActivity(i)
                        finish()
                    } else if (mapper == DeepLinkingManager.Mapper.OPEN_LIVE_SPECIFIC_CHANNEL) {
                        val channelString = Utility.getChannelNameFromUrl(data.toString())
                        if (channelString!!.isEmpty()) {
                            startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
                            finish()
                        } else {
                            RestClient(this@SplashActivity, Constants.API_BASE_URL + Constants.LIVE_QUERY + channelString, RestClient.Companion.Method.GET, null, object : NetworkOperationListener {
                                    override fun onSuccess(response: String?) {
                                        var response = response
                                        try {
                                            val arr = JSONArray(response)
                                            response = arr.getJSONObject(0).toString()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                        if (JSONParser.getLiveDetailsModel(response) != null) {
                                            val intent = Intent(
                                                this@SplashActivity,
                                                PlayerActivity::class.java
                                            )
                                            PlayerActivity.ARGS_VIDEO = null;
                                            PlayerActivity.ARGS_CHANNEL = JSONParser.getLiveDetailsModel(response);
                                            intent.putExtra("isGoonjActivity", true)
                                            startActivity(intent)
                                            finish()
                                        }
                                        else Utility.moveToSplashActivity(this@SplashActivity)
                                        finish()
                                    }

                                    override fun onFailed(code: Int, reason: String?) {
                                        Logger.println("Failed: $reason")
                                        Utility.moveToSplashActivity(this@SplashActivity)
                                        finish()
                                    }
                                }).exec()
                        }
                    } else if (mapper == DeepLinkingManager.Mapper.OPEN_TAB) {
                        val i = Intent(this@SplashActivity, WelcomeActivity::class.java)
                        i.putExtra("action", DeepLinkingManager.Mapper.OPEN_TAB)
                        startActivity(i)
                        finish()
                    } else {
                        // Default, open app
                        startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
                        finish()
                    }
                }
                else {
                    // Its VOD
                    RestClient(
                        this@SplashActivity,
                        Constants.API_BASE_URL + Constants.Companion.EndPoints.VOD_DETAILS + videoId,
                        RestClient.Companion.Method.GET,
                        null,
                        object : NetworkOperationListener {
                            override fun onSuccess(response: String?) {
                                val model: Video = JSONParser.getVodDetailsModel(response)!!
                                if (model != null && model.getCategory() == PaywallComedyFragment.SLUG) {
                                    /*val intent =
                                        Intent(this@SplashActivity, WelcomeActivity::class.java)
                                    intent.putExtra(
                                        "action",
                                        DeepLinkingManager.Mapper.OPEN_COMEDY_VIDEO
                                    )
                                    intent.putExtra(ARGS_VIDEO, model)
                                    intent.putExtra("isGoonjActivity", true)
                                    startActivity(intent)
                                    finish()*/
                                } else {
                                    if (model != null) {
                                        val intent = Intent(
                                            this@SplashActivity,
                                            PlayerActivity::class.java
                                        )
                                        PlayerActivity.ARGS_CHANNEL = null;
                                        PlayerActivity.ARGS_VIDEO = model;
                                        intent.putExtra("isGoonjActivity", true)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                WelcomeActivity::class.java
                                            )
                                        )
                                        finish()
                                    }
                                }
                            }

                            override fun onFailed(code: Int, reason: String?) {
                                startActivity(
                                    Intent(
                                        this@SplashActivity,
                                        WelcomeActivity::class.java
                                    )
                                )
                                finish()
                            }
                        }).exec()
                }
            }
        } else {
            if(mView.getPrefs().isInterestedTopicDone()){
                startActivity(Intent(this, WelcomeActivity::class.java));
                finish();
            }else{
                startActivity(Intent(this, GetStartedActivity::class.java));
                finish();
            }
        }
    }
}