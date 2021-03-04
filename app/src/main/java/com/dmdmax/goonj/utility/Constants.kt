package com.dmdmax.goonj.utility

import android.content.Context
import com.dmdmax.goonj.models.BitRatesModel
import com.dmdmax.goonj.storage.DBHelper
import com.dmdmax.goonj.storage.GoonjPrefs
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

public class Constants {

    companion object {

        var ACCESS_TOKEN = "1234"

        var IS_PAYMENT_PROCESSING_STARTED = false
        val ID = "_id"
        var OutOfCountryContext: Context? = null

        var IS_DATA_SYNCED_ON_SPLASH = false
        var IS_SSL_EXCEPTION = false
        var TAB_INDEX = -1
        var LIVE_CHANNEL_DEFAULT_INDEX = 0


        val CONFIG_EXPIRATION_TIME_IN_SEC: Long = 300
        val LIVE_QUERY = "/live?slug="
        val PIC_EXTENSION = ".webp"
        val VOD_THUMB = "video/thumb/"
        val LIVE_LOGO = "live/logo/"
        val ANCHOR_PIC = "anchor/"
        val PROGRAMS_PIC = "programs/"
        val CATEGORY_PIC = "categories/"
        val VOD_END_URL = ".urlset/master.m3u8"
        val GOONJ_URL = "goonj.pk/"
        val CHANNEL = "channel/"
        val SUB_AFTER_UNSUB =
            "You still have time remaining on your subscription, do you want to subscribe again for same package?"
        val LOCATION_PERMISSION_TEXT =
            "Goonj would like to access your location. For a better streaming experience, please enable location sharing."


        /*
            ================================
                    REMOTE CONFIG
            ================================
         */
        var CORONA_BANNER_VISIBILITY = false
        var CORONA_HOTLINE_IMAGE: String? = null
        var FORCE_UPDATE_VERSION = 0
        var PAYWALL_BASE_URL: String? = null
        var FLAGGED_KEYWORDS: List<String> = ArrayList()
        var IN_FEED_AD_FREQ = 0
        var VOD_ADTAG_SOURCE: String? = null
        var EXCLUDE_PRE_ROLL_CHANNELS: List<String> = ArrayList()
        var IS_PAYWALL_ENABLED = false
        var HE_URL: String? = null
        var CATEGORIES_STRING_JSON: String? = null
        var CATEGORIES_CHANNEL_JSON: String? = null
        var COMEDY_CATEGORIES_STRING_JSON: String? = null
        var COMEDY_BASE_URL: String? = null
        var COMEDY_API_KEY: String? = null
        var VOD_POSTFIX_URL: String? = null
        lateinit var DEFAULT_AKAMAI_TOKEN: String;
        var IS_LIVE_PRE_ROLL_ENABLED = false
        var LANDING_PAGE_320x100_AD_UNIT_ID: String? = null
        var SECURITY_ACL: String? = null
        var SECURITY_TOKEN_NAME: String? = null
        var SECURITY_WINDOW: String? = null
        var SECURITY_KEY: String? = null
        var CHECK_SUBS_STATUS_INTERVAL: Long = 0
        var CHECK_SMALL_SUBS_STATUS_INTERVAL: Long = 0
        var DOUBLE_CLICK_320X50_AD_UNIT_ID: String? = null
        var DOUBLE_CLICK_300X250_AD_UNIT_ID: String? = null
        var LIKE_VIDEO_URL: String? = null
        var CHECK_LIKE_URL: String? = null
        var FETCH_TOTAL_LIKES_URL: String? = null
        var REPORTING_URL: String? = null
        var PRE_ROLL_TAG: String? = null
        var PREROLL_VOD_FREQ = 0
        var PREROLL_LIVE_FREQ = 0
        var LIVE_URL: String? = null
        var VOD_URL: String? = null
        var API_BASE_URL: String? = null
        var CDN_STATIC_URL: String? = null
        var TERMS_URL: String? = null
        var PRIVACY_POLICY_URL: String? = null
        var FEED_LIMIT = 0
        var IN_FEED_BANNER_AD_FREQ: String? = null
        lateinit var UPDATE_AVAILABLE_IMAGE: String;
        var FORCE_UPDATE: Boolean = false
        var IS_ONLY_MESSAGE: Boolean? = null
        var LATEST_VERSION = 0
        var IS_UPDATE_AVAILABLE: Boolean = false;
        lateinit var UPDATE_MESSAGE: String;
        var CONTACT_US_NUMBER: String? = null


        object EndPoints {
            const val POST_VIDEO_VIEWS = "video/views"
            const val POST_LIVE_VIEWS = "live/views"
            const val GET_ANCHOR_VIDEOS = "video?anchor="
            const val GET_PROGRAM_VIDEOS = "video?program="
            const val GET_TOPIC_VIDEOS = "video?topics="
            const val GET_CHANNEL_VIDEOS = "video?source="
            const val VOD_DETAILS = "video?_id="
            const val VIDEO_BY_CATEGORY = "video?category="
            const val VIDEO = "video"
            const val ANCHOR = "anchor"
            const val LIVE = "live"
            const val SEARCH = "search"
            const val TOPIC = "topic"
            const val PROGRAM = "program"
            const val CATEGORY = "category"
            const val BANNER = "banner/list"

            // Paywall
            const val STATUS = "payment/status"
            const val PACKAGE = "package"
            const val PAYWALL = "paywall"
            const val SEND_OTP = "payment/otp/send"
            const val VERIFY_OTP = "payment/otp/verify"
            const val SUBSCRIBE = "payment/subscribe"
            const val RECHARGE = "payment/recharge"
            const val UN_SUBSCRIBE = "payment/unsubscribe"

            // Feedback
            const val QUESTION = "questions"
            const val ANSWER = "answer"

            // Auth
            const val REFRESH_TOKEN = "auth/refresh"

            // Comedy
            const val GET_EPISODES = "rest-api/v100/episodes"
            const val SEND_COMEDY_OTP = "rest-api/v100/signin"
            const val GET_COMEDY_PACKAGES = "rest-api/v100/packages"
            const val COMEDY_VERIFY_OTP = "rest-api/v100/verify_otp"
            const val CHECK_SUBSCRIPTION = "rest-api/v100/check_subscription"
            const val CANCEL_COMEDY_SUBSCRIPTION = "rest-api/v100/cancel_subscription"
        }

        private var currentWindow = 0
        private var playbackPos: Long = 0
    }

    fun setPlayerState(currentWindow: Int, playbackPos: Long) {
        Constants.currentWindow = currentWindow
        Constants.playbackPos = playbackPos
    }

    fun getCurrentWindow(): Int {
        return currentWindow
    }

    fun getPlaybackPos(): Long {
        return playbackPos
    }


    fun getVodStreamingLink(filename: String): String? {
        Logger.println("filename: $filename")
        return VOD_URL + "/smil:" + removeFileExtension(filename) + "/playlist.m3u8"
    }

    fun getNewVodStreamingLink(filename: String): String? {
        return VOD_URL + removeNewFileExtension(filename) + VOD_POSTFIX_URL + getNewFileExtension(
            filename
        ) + VOD_END_URL
    }

    fun getNewFileExtension(filename: String): String {
        return "." + filename.split("\\.").toTypedArray()[filename.split("\\.")
            .toTypedArray().size - 1]
    }

    fun getExtension(filename: String?): String? {
        if (filename != null) {
            val splitString = filename.split("\\.").toTypedArray()
            return "." + splitString[splitString.size - 1]
        }
        return ".m3u8"
    }

    private fun removeFileExtension(filename: String?): String {
        return if (filename != null) {
            val splitString = filename.split("\\.").toTypedArray()
            filename.replace(splitString[splitString.size - 1], "smil")
        } else {
            ""
        }
    }

    private fun removeNewFileExtension(filename: String?): String {
        return filename?.split("\\.")?.toTypedArray()?.get(0) ?: ""
    }

    object NewBitRates {
        const val BITRATE_AUTO = "Auto"
        const val BITRATE_DATA_SAVER = "Data Saver"
        const val BITRATE_MEDIUM = "Medium"
        const val BITRATE_HIGH = "High"
    }

    fun getNewBitrates(context: Context?): ArrayList<BitRatesModel>? {
        val prefs = GoonjPrefs(context)
        val bitRates: ArrayList<BitRatesModel> = ArrayList<BitRatesModel>()
        bitRates.add(
            BitRatesModel(
                0,
                NewBitRates.BITRATE_AUTO,
                prefs.getGlobalBitrate().equals(NewBitRates.BITRATE_AUTO)
            )
        )
        bitRates.add(
            BitRatesModel(
                1,
                NewBitRates.BITRATE_DATA_SAVER,
                prefs.getGlobalBitrate().equals(NewBitRates.BITRATE_DATA_SAVER)
            )
        )
        bitRates.add(
            BitRatesModel(
                2,
                NewBitRates.BITRATE_MEDIUM,
                prefs.getGlobalBitrate().equals(NewBitRates.BITRATE_MEDIUM)
            )
        )
        bitRates.add(
            BitRatesModel(
                3,
                NewBitRates.BITRATE_HIGH,
                prefs.getGlobalBitrate().equals(NewBitRates.BITRATE_HIGH)
            )
        )
        return bitRates
    }

    object ThumbnailManager {
        fun getVodThumbnail(filename: String?): String {
            return Constants.CDN_STATIC_URL + VOD_THUMB + filename
        }

        fun getLiveThumbnail(filename: String?): String {
            return CDN_STATIC_URL + LIVE_LOGO + filename
        }

        fun getIconThumbs(name: String, tag: Int): String {
            var nameForUrl = name.replace(' ', '-')
            nameForUrl = nameForUrl.replace(".", "")
            try {
                nameForUrl = URLEncoder.encode(nameForUrl, "utf-8")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            return if (tag == DBHelper.Companion.Tags.TAG_ANCHOR) Constants.CDN_STATIC_URL + Constants.ANCHOR_PIC + nameForUrl + PIC_EXTENSION else if (tag == DBHelper.Companion.Tags.TAG_PROGRAM) Constants.CDN_STATIC_URL + PROGRAMS_PIC + nameForUrl + PIC_EXTENSION else if (tag == DBHelper.Companion.Tags.TAG_CATEGORY) Constants.CDN_STATIC_URL + Constants.CATEGORY_PIC + name+".png" else "";
        }
    }
}