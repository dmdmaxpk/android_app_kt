package com.dmdmax.goonj.firebase_events

import android.content.Context
import android.os.Bundle
import com.dmdmax.goonj.utility.Logger
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics

open class EventManager {

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics;
    private var mContext: Context;

    private constructor(context: Context){
        this.mContext = context;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    companion object {
        private var instance: EventManager? = null;

        fun getInstance(context: Context): EventManager {
            if(this.instance == null){
                this.instance = EventManager(context);
            }

            return this.instance!!;
        }
    }

    open fun triggerFacebookSubscribeEvent(packageName: String?, slug: String, paymentSource: String?) {
        val logger: AppEventsLogger = AppEventsLogger.newLogger(mContext)

        val params = Bundle()
        params.putString("package_subscribed", packageName!!.toLowerCase().replace(" ", "_"))
        params.putString("paywall_type", slug.toLowerCase())

        if(paymentSource != null) {
            params.putString("paywall_source", slug.toLowerCase())
        }

        Logger.println("FACEBOOK EVENT: $params")
        logger.logEvent(AppEventsConstants.EVENT_NAME_SUBSCRIBE, params)
    }

    fun fireEvent(event: String){
        mFirebaseAnalytics.logEvent("V2_${event}", null)
        Logger.println("V2_${event}");
    }

    object Events {
        // Category View
        const val VIEW = "_View"

        // Player
        const val PLAY_LIVE = "Play_Live"
        const val PLAY_VOD = "Play_Vod"
        const val PLAY_CONTENT = "Play_Content_"

        // Get Started
        const val GET_STARTED_VIEW = "GetStarted_View"
        const val GET_STARTED_SKIP = "Click_GetStarted_Skip"
        const val GET_STARTED_JOIN_NOW = "Click_GetStarted_Join_Now"
        const val GET_STARTED_SCREEN_SWIPED = "Click_GetStarted_Screen_Swiped"

        // Bottom Menu
        const val BOTTOM_MENU_HOME_CLICKED = "Bottom_Menu_Home_Screen_Clicked"
        const val BOTTOM_MENU_LIVE_CLICKED = "Bottom_Menu_Live_Screen_Clicked"
        const val BOTTOM_MENU_VOD_CLICKED = "Bottom_Menu_Vod_Screen_Clicked"
        const val BOTTOM_MENU_MORE_CLICKED = "Bottom_More_Screen_Clicked"

        const val BOTTOM_MENU_HOME_VIEW = "Bottom_Menu_Home_Screen_View"
        const val BOTTOM_MENU_LIVE_VIEW = "Bottom_Menu_Live_Screen_View"
        const val BOTTOM_MENU_VOD_VIEW = "Bottom_Menu_Vod_Screen_View"
        const val BOTTOM_MENU_MORE_VIEW = "Bottom_More_Screen_View"

        // Goonj paywall
        const val GOONJ_PAYWALL_PAY_CLICK = "Goonj_Paywall_Pay_Click"
        const val GOONJ_PAYWALL_OTP_SENT = "Goonj_Paywall_Otp_Sent"
        const val GOONJ_PAYWALL_OTP_VERIFIED = "Goonj_Paywall_Otp_Verified"
        const val GOONJ_PAYWALL_OTP_NOT_VERIFIED = "Goonj_Paywall_Not_Verified"
        const val GOONJ_PAYWALL_SUBSCRIBED = "Goonj_Paywall_Subscribed"
        const val GOONJ_PAYWALL_MESSAGE = "Goonj_Paywall_Message_"
        const val GOONJ_PAYWALL_FAILED_TO_SUBSCRIBE = "Goonj_Paywall_Failed_To_Subscribe"
        const val GOONJ_PAYWALL_TRIAL_ACTIVATED = "Goonj_Paywall_Trial_Activated"
        const val GOONJ_PAYWALL_ALREADY_SUBSCRIBED = "Goonj_Paywall_Already_Subscribed"
        const val GOONJ_PAYWALL_UNSUB_CLICK = "Goonj_Paywall_Unsubscribe_Click"
        const val GOONJ_PAYWALL_UNSUB = "Goonj_Paywall_Unsubscribed"

        // Comedy paywall
        const val COMEDY_PAYWALL_PAY_CLICK = "Comedy_Paywall_Pay_Click"
        const val COMEDY_PAYWALL_OTP_SENT = "Comedy_Paywall_Otp_Sent"
        const val COMEDY_PAYWALL_FAILED_TO_SENT_OTP_SENT = "Comedy_Paywall_Failed_To_Sent_Otp_Reason_"
        const val COMEDY_PAYWALL_OTP_VERIFIED = "Comedy_Paywall_Otp_Verified"
        const val COMEDY_PAYWALL_OTP_NOT_VERIFIED = "Comedy_Paywall_Otp_Not_Verified"
        const val COMEDY_PAYWALL_SUBSCRIBED = "Comedy_Paywall_Subscribed"
        const val COMEDY_PAYWALL_FAILED_TO_SUBSCRIBE = "Comedy_Paywall_Failed_To_Subscribe"
        const val COMEDY_PAYWALL_ALREADY_SUBSCRIBED = "Comedy_Paywall_Already_Subscribed"
        const val COMEDY_PAYWALL_UNSUB_CLICK = "Comedy_Paywall_Unsubscribe_Click"
        const val COMEDY_PAYWALL_UNSUB = "Comedy_Paywall_Unsubscribed"

        // Binjee paywall
        const val BINJEE_PAYWALL_PAY_CLICK = "Binjee_Paywall_Pay_Click"
        const val BINJEE_PAYWALL_OTP_SENT = "Binjee_Paywall_Otp_Sent"
        const val BINJEE_PAYWALL_FAILED_TO_SENT_OTP_SENT = "Binjee_Paywall_Failed_To_Sent_Otp_Reason_"
        const val BINJEE_PAYWALL_OTP_VERIFIED = "Binjee_Paywall_Otp_Verified"
        const val BINJEE_PAYWALL_OTP_NOT_VERIFIED = "Binjee_Paywall_Otp_Not_Verified"
        const val BINJEE_PAYWALL_SUBSCRIBED = "Binjee_Paywall_Subscribed"
        const val BINJEE_PAYWALL_FAILED_TO_SUBSCRIBE = "Binjee_Paywall_Failed_To_Subscribe"
        const val BINJEE_PAYWALL_ALREADY_SUBSCRIBED = "Binjee_Paywall_Already_Subscribed"
        const val BINJEE_PAYWALL_UNSUB_CLICK = "Binjee_Paywall_Unsubscribe_Click"
        const val BINJEE_PAYWALL_UNSUB = "Binjee_Paywall_Unsubscribed"
    }
}