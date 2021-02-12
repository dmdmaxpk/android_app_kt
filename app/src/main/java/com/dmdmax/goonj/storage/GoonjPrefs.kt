package com.dmdmax.goonj.storage

import android.content.Context
import android.content.SharedPreferences
import android.provider.MediaStore
import com.dmdmax.goonj.network.responses.*
import com.dmdmax.goonj.screens.fragments.ChannelsFragment
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.collections.ArrayList

class GoonjPrefs {
    private var prefs: SharedPreferences? = null;
    private var editor: SharedPreferences.Editor? = null;

    private val KEY_RECENT_VIDEOS = "recent_videos"
    private val KEY_ANCHORS = "anchors"
    private val KEY_PROGRAMS = "programs"
    private val KEY_CHANNELS = "channels"
    private val KEY_PACKAGES = "packages"
    private val KEY_TOPICS = "topics"
    private val KEY_PREMIUM = "premium"
    private val KEY_LOCATION_PERMISSION = "local_permission"

    constructor(context: Context?){
        if (context != null) {
            prefs = context.getSharedPreferences("goonjPrefs", Context.MODE_PRIVATE);
            editor = prefs!!.edit();
        }
    }


    // Setters
    fun setAnchors(anchors: List<Anchor?>?) {
        editor!!.putString(KEY_ANCHORS, getString(anchors)).commit()
    }

    fun setPrograms(programs: List<Program?>?) {
        editor!!.putString(KEY_PROGRAMS, getString(programs)).commit()
    }

    fun setRecentVideos(videos: List<MediaStore.Video?>?) {
        editor!!.putString(KEY_RECENT_VIDEOS, getString(videos)).commit()
    }

    fun setTopics(topics: List<Topic?>?) {
        editor!!.putString(KEY_TOPICS, getString(topics)).commit()
    }

    fun setChannels(channels: List<Channel?>?) {
        editor!!.putString(KEY_CHANNELS, getString(channels)).commit()
    }

    fun setPremium(videos: List<Video?>?) {
        editor!!.putString(KEY_PREMIUM, getString(videos)).commit()
    }

    // Getters
    fun getAnchors(): List<Anchor?>? {
        return getAnchorsList(prefs!!.getString(KEY_ANCHORS, ""))
    }

    fun getPrograms(): List<Program?>? {
        return getProgramsList(prefs!!.getString(KEY_PROGRAMS, ""))
    }

    fun getRecentVideos(limit: Int): List<Video>? {
        val list: List<Video>? = getVideosList(prefs!!.getString(KEY_RECENT_VIDEOS, ""))
        return if (list != null && !list.isEmpty() && list.size > limit) {
            list.subList(0, limit)
        } else list
    }

    fun getTopics(): List<Topic?>? {
        return getTopics(prefs!!.getString(KEY_TOPICS, ""))
    }

    fun getChannels(): List<Channel?>? {
        return getChannels(prefs!!.getString(KEY_CHANNELS, ""))
    }

    fun getPremiumList(): List<Video>? {
        return getVideosList(prefs!!.getString(KEY_PREMIUM, ""))
    }

    fun setMsisdn(number: String, slug: String) {
        Logger.println("Prefs - setMsisdn - $number - $slug")
        if (slug == ChannelsFragment.SLUG) {
            setLiveMsisdn(number)
        } else {
            setComedyMsisdn(number)
        }
    }

    fun getMsisdn(slug: String): String? {
        return if (slug == ChannelsFragment.SLUG) {
            getLiveMsisdn()
        } else {
            getComedyMsisdn()
        }
    }

    public fun setIsSkipped(skipped: Boolean) {
        editor!!.putBoolean("skipped", skipped).commit()
    }

    public fun isSkipped(): Boolean {
        return prefs!!.getBoolean("skipped", false)
    }

    public fun setIsInterestedTopicDone(skipped: Boolean) {
        editor!!.putBoolean("interested", skipped).commit()
    }

    public fun isInterestedTopicDone(): Boolean {
        return prefs!!.getBoolean("interested", false)
    }

    private fun setLiveMsisdn(number: String) {
        editor!!.putString("cellNum", number).commit()
    }

    private fun setComedyMsisdn(number: String) {
        editor!!.putString("comedyCellNum", number).commit()
    }

    private fun getLiveMsisdn(): String? {
        return prefs!!.getString("cellNum", null)
    }

    private fun getComedyMsisdn(): String? {
        return prefs!!.getString("comedyCellNum", null)
    }

    fun getLocationPermissionCounter(): Int {
        return prefs!!.getInt(KEY_LOCATION_PERMISSION, 0)
    }

    fun setOtpValidated(value: Boolean) {
        editor!!.putBoolean("otpValidated", value).commit()
    }

    fun isOtpValidated(): Boolean {
        return prefs!!.getBoolean("otpValidated", false)
    }

    fun setSubscriptionStatus(value: String?, slug: String) {
        if (slug == ChannelsFragment.SLUG) {
            setLiveSubscriptionStatus(value)
        } else {
            setComedySubscriptionStatus(value)
        }
    }

    fun getSubscriptionStatus(slug: String): String? {
        return if (slug == ChannelsFragment.SLUG) {
            getLiveSubscriptionStatus()
        } else {
            getComedySubscriptionStatus()
        }
    }

    fun setLiveSubscriptionStatus(value: String?) {
        editor!!.putString("subscriptionStatus", value).commit()
    }

    fun getLiveSubscriptionStatus(): String? {
        return prefs!!.getString("subscriptionStatus", "");
    }

    fun setComedySubscriptionStatus(value: String?) {
        editor!!.putString("ComedySubscriptionStatus", value).commit()
    }

    fun getComedySubscriptionStatus(): String? {
        return prefs!!.getString("ComedySubscriptionStatus", "");
    }

    fun setPaymentProcessing(value: Boolean, slug: String) {
        if (slug == ChannelsFragment.SLUG) {
            setLivePaymentProcessing(value)
        } else {
            setComedyPaymentProcessing(value)
        }
    }

    fun isPaymentProcessing(slug: String): Boolean {
        return if (slug == ChannelsFragment.SLUG) {
            isLivePaymentProcessing()
        } else {
            isComedyPaymentProcessing()
        }
    }

    fun setLivePaymentProcessing(value: Boolean) {
        editor!!.putBoolean("isProcessing", value).commit()
    }

    fun isLivePaymentProcessing(): Boolean {
        return prefs!!.getBoolean("isProcessing", false)
    }

    fun setComedyPaymentProcessing(value: Boolean) {
        editor!!.putBoolean("isComedyProcessing", value).commit()
    }

    fun isComedyPaymentProcessing(): Boolean {
        return prefs!!.getBoolean("isComedyProcessing", false)
    }


    fun setAutoRenewal(value: Boolean, slug: String) {
        if (slug == ChannelsFragment.SLUG) {
            setLiveAutoRenewal(value)
        } else {
            setComedyAutoRenewal(value)
        }
    }

    fun isAutoRenewal(slug: String): Boolean {
        return if (slug == ChannelsFragment.SLUG) {
            isLiveAutoRenewal()
        } else {
            isComedyAutoRenewal()
        }
    }

    fun setLiveAutoRenewal(value: Boolean) {
        editor!!.putBoolean("autoRenewal", value).commit()
    }

    fun isLiveAutoRenewal(): Boolean {
        return prefs!!.getBoolean("autoRenewal", false)
    }

    fun setComedyAutoRenewal(value: Boolean) {
        editor!!.putBoolean("comedyAutoRenewal", value).commit()
    }

    fun isComedyAutoRenewal(): Boolean {
        return prefs!!.getBoolean("comedyAutoRenewal", false)
    }


    fun setStreamable(value: Boolean, slug: String) {
        if (slug == ChannelsFragment.SLUG) {
            setLiveStreamable(value)
        } else {
            setComedyStreamable(value)
        }
    }

    fun isStreamable(slug: String): Boolean {
        return if (slug == ChannelsFragment.SLUG) {
            isLiveStreamable()
        } else {
            isComedyStreamable()
        }
    }

    fun setLiveStreamable(value: Boolean) {
        editor!!.putBoolean("isStreamable", value).commit()
    }

    fun isLiveStreamable(): Boolean {
        return prefs!!.getBoolean("isStreamable", false)
    }

    fun setComedyStreamable(value: Boolean) {
        editor!!.putBoolean("isComedyStreamable", value).commit()
    }

    fun isComedyStreamable(): Boolean {
        return prefs!!.getBoolean("isComedyStreamable", false)
    }

    fun setUserId(value: String?) {
        editor!!.putString("user_id", value).commit()
    }

    fun getUserId(): String? {
        return prefs!!.getString("user_id", "null")
    }

    fun setSubscribedPackageId(value: String?, slug: String) {
        if (slug == ChannelsFragment.SLUG) {
            setLiveSubscribedPackageId(value)
        } else {
            setComedySubscribedPackageId(value)
        }
    }

    fun getSubscribedPackageId(slug: String): String? {
        return if (slug == ChannelsFragment.SLUG) {
            getLiveSubscribedPackageId()
        } else {
            getComedySubscribedPackageId()
        }
    }

    fun setPaymentSource(value: String?, slug: String) {
        if (slug == ChannelsFragment.SLUG) {
            setLivePaymentSource(value)
        } else {
            setComedyPaymentSource(value)
        }
    }

    fun getPaymentSource(slug: String): String? {
        return if (slug == ChannelsFragment.SLUG) {
            getLivePaymentSource()
        } else {
            getComedyPaymentSource()
        }
    }


    fun setLiveSubscribedPackageId(value: String?) {
        editor!!.putString("subPackageId", value).commit()
    }

    fun getLiveSubscribedPackageId(): String? {
        return prefs!!.getString("subPackageId", "")
    }


    fun setComedySubscribedPackageId(value: String?) {
        editor!!.putString("comedySubPackageId", value).commit()
    }

    fun getComedySubscribedPackageId(): String? {
        return prefs!!.getString("comedySubPackageId", "")
    }

    fun setLivePaymentSource(value: String?) {
        editor!!.putString("livePaymentSource", value).commit()
    }

    fun getLivePaymentSource(): String? {
        return prefs!!.getString("livePaymentSource", "")
    }

    fun setComedyPaymentSource(value: String?) {
        editor!!.putString("comedyPaymentSource", value).commit()
    }

    fun getComedyPaymentSource(): String? {
        return prefs!!.getString("comedyPaymentSource", "")
    }

    fun setFirstTime(value: Boolean) {
        editor!!.putBoolean("isFirstTime", value).commit()
    }

    fun isFirstTime(): Boolean {
        return prefs!!.getBoolean("isFirstTime", true)
    }


    fun setGlobalBitrate(globalBitrate: String?) {
        if (editor != null) editor!!.putString("globalBitrate", globalBitrate).commit()
    }

    fun getGlobalBitrate(): String? {
        return if (prefs != null) prefs!!.getString(
            "globalBitrate",
            Constants.NewBitRates.BITRATE_AUTO
        ) else Constants.NewBitRates.BITRATE_AUTO
    }

    fun setFcmToken(token: String?) {
        editor!!.putString("fcmToken", token).commit()
    }

    fun getFcmToken(): String? {
        return prefs!!.getString("fcmToken", null)
    }

    fun setAccessToken(token: String) {
        Constants.ACCESS_TOKEN = token
        editor!!.putString("accessToken", token).commit()
    }

    fun getAccessToken(): String? {
        return prefs!!.getString("accessToken", null)
    }

    fun setRefreshToken(token: String) {
        Logger.println("setRefreshToken: $token")
        editor!!.putString("refreshToken", token).commit()
    }

    fun getRefreshToken(): String? {
        return prefs!!.getString("refreshToken", "")
    }


    fun setOpenAppCounter(number: Int) {
        editor!!.putInt("setOpenAppCounter", number).commit()
    }

    fun getOpenAppCounter(): Int {
        return prefs!!.getInt("setOpenAppCounter", 0)
    }

    fun setFeedbackSubmitted(value: Boolean) {
        editor!!.putBoolean("setFeedbackSubmitted", value).commit()
    }

    fun getFeedbackSubmitted(): Boolean {
        return prefs!!.getBoolean("setFeedbackSubmitted", false)
    }


    fun getPackagesList(): List<PaywallPackage> {
        return getPackagesList(prefs!!.getString(KEY_PACKAGES, ""))
    }

    // Helper methods
    private fun getString(list: List<*>?): String? {
        return if (list == null) "" else Gson().toJson(list)
    }

    private fun getVideosList(json: String?): List<Video>? {
        if (json == null || json.isEmpty()) return null
        val gson = Gson()
        return gson.fromJson<List<Video>>(json, object : TypeToken<List<Video?>?>() {}.type)
    }

    private fun getPackagesList(json: String?): List<PaywallPackage> {
        if (json == null || json.isEmpty()) return ArrayList<PaywallPackage>();
        val gson = Gson()
        return gson.fromJson(json, object : TypeToken<List<PaywallPackage?>?>() {}.type)
    }

    private fun getAnchorsList(json: String?): List<Anchor?>? {
        val gson = Gson()
        return gson.fromJson<List<Anchor?>>(json, object : TypeToken<List<Anchor?>?>() {}.type)
    }

    private fun getProgramsList(json: String?): List<Program?>? {
        val gson = Gson()
        return gson.fromJson<List<Program?>>(json, object : TypeToken<List<Program?>?>() {}.type)
    }

    private fun getTopics(json: String?): List<Topic?>? {
        val gson = Gson()
        return gson.fromJson<List<Topic?>>(json, object : TypeToken<List<Topic?>?>() {}.type)
    }

    private fun getChannels(json: String?): List<Channel?>? {
        return if (json != null && !json.isEmpty()) {
            val gson = Gson()
            gson.fromJson<List<Channel?>>(json, object : TypeToken<List<Channel?>?>() {}.type)
        } else {
            ArrayList<Channel?>()
        }
    }
}