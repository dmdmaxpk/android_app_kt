package com.dmdmax.goonj.screens.implements

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.screens.views.SplashView
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Constants.ThumbnailManager.getVodThumbnail
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Utility
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.JsonObject
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.lang.Exception
import com.dmdmax.goonj.utility.*
import com.dmdmax.goonj.models.Params
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.network.client.NetworkOperationListener;
import com.dmdmax.goonj.network.client.RestClient;
import com.dmdmax.goonj.receivers.NotificationListener
import java.io.Console

class SplashViewImpl: BaseObservableView<SplashView.Listener>, SplashView {

    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig;
    private lateinit var mPrefs: GoonjPrefs;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.activity_splash, parent, false));
    }

    override fun getRemoteConfigs() {
        mPrefs = GoonjPrefs(getContext());
        val versionCode: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getContext().packageManager.getPackageInfo(getContext().packageName, 0).longVersionCode.toInt();
        } else {
            getContext().packageManager.getPackageInfo(getContext().packageName, 0).versionCode
        };

        Logger.println("LONG VERSION CODE: $versionCode")

        if(versionCode == 3018 && !mPrefs.isFlushedPreviousFcmToken() && (mPrefs.getFcmToken() != null)){
            getPrefs().setFcmToken(null);
            mPrefs.flushPreviousFcmToken();
            Logger.println("FCM TOKEN FLUSHED");
        }else{
            Logger.println("FCM TOKEN ALREADY FLUSHED");
        }

        if (getPrefs().getFcmToken() == null) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Logger.println("Fetching FCM registration token failed" + task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                mPrefs.setFcmToken(token)
                Logger.println("NEW FCM TOKEN: $token");
                Utility.sendRegistrationToServer(getContext(), token);
            });
        } else {
            Logger.println("OLD FCM TOKEN: ${getPrefs().getFcmToken()}")
            Handler().postDelayed(Runnable {
                Utility.sendActivityToServer(getContext(), mPrefs.getUserId(PaywallGoonjFragment.SLUG));
            }, 3000)
        }


        // HERE WE ARE TAKING THE REFERENCE OF OUR IMAGE
        // SO THAT WE CAN PERFORM ANIMATION USING THAT IMAGE
        val backgroundImage: ImageView = findViewById(R.id.splash_image)
        val slideAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.splash_screen)
        backgroundImage.startAnimation(slideAnimation)

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler().postDelayed({
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
            mFirebaseRemoteConfig.setConfigSettingsAsync(FirebaseRemoteConfigSettings.Builder().build())
            mFirebaseRemoteConfig.fetch(Constants.CONFIG_EXPIRATION_TIME_IN_SEC)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Utility.setConstants(mFirebaseRemoteConfig)
                        Logger.println("Fetch successful")
                        mFirebaseRemoteConfig.activate();
                        versionChecker();
                    }
                }.addOnFailureListener { e ->
                    Logger.println("Fetch failed: " + e.message)
                    e.printStackTrace()
                    Utility.setConstants(mFirebaseRemoteConfig)
                    versionChecker()
                }
        }, 3000)
    }

    private fun versionChecker() {
        if (Constants.IS_ONLY_MESSAGE!!) {
            showPopup(
                Constants.UPDATE_MESSAGE,
                Constants.IS_UPDATE_AVAILABLE,
                Constants.FORCE_UPDATE,
                Constants.UPDATE_AVAILABLE_IMAGE,
                Constants.LATEST_VERSION
            )
        } else {
            workingCompleted()
        }
    }

    private fun showPopup(text: String, update: Boolean, force: Boolean, banner: String, latestVersionCode: Int) {
        if (update && force) {
            try {
                val currentVersionCode = getContext().packageManager.getPackageInfo(
                    getContext().packageName,
                    0
                ).versionCode
                if (currentVersionCode < latestVersionCode || currentVersionCode == Constants.FORCE_UPDATE_VERSION) {
                    showForcePopup(text, banner)
                } else {
                    workingCompleted()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (update) {
            try {
                val currentVersionCode = getContext().packageManager.getPackageInfo(
                    getContext().packageName,
                    0
                ).versionCode
                if (currentVersionCode >= latestVersionCode) {
                    workingCompleted()
                } else {
                    showSimpleUpdate(text, banner)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (force) {
            showNotification(text, banner)
        } else {
            showSimpleMessage(text, banner)
        }
    }

    private fun showSimpleUpdate(textMessage: String, banner: String) {
        val view: View = LayoutInflater.from(getContext()).inflate(R.layout.popup_layout, null)
        val bannerImage = view.findViewById<ImageView>(R.id.bannerImage)
        val smallPb = view.findViewById<ProgressBar>(R.id.smallPb)
        val text = view.findViewById<TextView>(R.id.text)
        text.text = textMessage
        val builder = AlertDialog.Builder(getContext(), R.style.CustomAlertDialogTheme)
        builder.setView(view)
        Picasso.get().load(getVodThumbnail(banner))
            .into(bannerImage, object : Callback {
                override fun onSuccess() {
                    smallPb.visibility = View.GONE
                }

                override fun onError(e: Exception) {
                    bannerImage.setImageResource(R.drawable.no_image_found)
                    smallPb.visibility = View.GONE
                }
            })
        builder.setPositiveButton(
            "Continue"
        ) { dialog, which ->
            gotoPlayStore()
            (getContext() as BaseActivity).finish()
        }
        builder.setNegativeButton(
            "Later"
        ) { dialog, which ->
            dialog.dismiss()
            workingCompleted()
        }
        builder.create().show()
    }

    private fun showForcePopup(textMessage: String, banner: String) {
        val view: View = LayoutInflater.from(getContext()).inflate(R.layout.popup_layout, null)
        val bannerImage = view.findViewById<ImageView>(R.id.bannerImage)
        val smallPb = view.findViewById<ProgressBar>(R.id.smallPb)
        val text = view.findViewById<TextView>(R.id.text)
        text.text = textMessage
        val builder = AlertDialog.Builder(getContext(), R.style.CustomAlertDialogTheme)
        builder.setCancelable(false)
        builder.setView(view)
        bannerImage.setImageResource(R.drawable.new_update)
        smallPb.visibility = View.GONE
        builder.setPositiveButton(
            "Continue"
        ) { dialog, which ->
            gotoPlayStore()
            (getContext() as BaseActivity).finish()
        }
        builder.setNegativeButton(
            "Quit"
        ) { dialog, which ->
            dialog.dismiss()
            (getContext() as BaseActivity).finish()
        }
        builder.create().show()
    }

    private fun showNotification(textMessage: String, banner: String) {
        val view: View = LayoutInflater.from(getContext()).inflate(R.layout.popup_layout, null)
        val bannerImage = view.findViewById<ImageView>(R.id.bannerImage)
        val smallPb = view.findViewById<ProgressBar>(R.id.smallPb)
        val text = view.findViewById<TextView>(R.id.text)
        text.text = textMessage
        val builder = AlertDialog.Builder(getContext(), R.style.CustomAlertDialogTheme)
        builder.setCancelable(false)
        builder.setView(view)
        Picasso.get().load(getVodThumbnail(banner))
            .into(bannerImage, object : Callback {
                override fun onSuccess() {
                    smallPb.visibility = View.GONE
                }

                override fun onError(e: Exception) {
                    smallPb.visibility = View.GONE
                    bannerImage.visibility = View.GONE
                }
            }
            )
        builder.setPositiveButton(
            "Quit"
        ) { dialog, which ->
            dialog.dismiss()
            (getContext() as BaseActivity).finish()
        }
        builder.create().show()
    }

    private fun showSimpleMessage(textMessage: String, banner: String) {
        val view: View = LayoutInflater.from(getContext()).inflate(R.layout.popup_layout, null)
        val bannerImage = view.findViewById<ImageView>(R.id.bannerImage)
        val smallPb = view.findViewById<ProgressBar>(R.id.smallPb)
        val text = view.findViewById<TextView>(R.id.text)
        text.text = textMessage
        val builder = AlertDialog.Builder(getContext(), R.style.CustomAlertDialogTheme)
        builder.setCancelable(false)
        builder.setView(view)
        Picasso.get().load(getVodThumbnail(banner))
            .into(bannerImage, object : Callback {
                override fun onSuccess() {
                    smallPb.visibility = View.GONE
                }

                override fun onError(e: Exception) {
                    bannerImage.setImageResource(R.drawable.no_image_found)
                    smallPb.visibility = View.GONE
                }
            }
            )
        builder.setPositiveButton("Ok") { dialog, which ->
            dialog.dismiss()
            workingCompleted()
        }
        builder.create().show()
    }

    private fun gotoPlayStore() {
        val appPackageName =
            getContext().packageName // getPackageName() from Context or Activity object
        try {
            getContext().startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            getContext().startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    private fun workingCompleted() {
        for (listener in getListeners()) {
            listener.onCompleted()
        }
    }
}