package com.dmdmax.goonj.utility

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import android.text.format.DateUtils
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.Params
import com.dmdmax.goonj.screens.activities.SplashActivity
import com.dmdmax.goonj.storage.GoonjPrefs
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONStringer
import java.math.BigDecimal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class Utility {

    companion object{
        val TAG = "Goonj"

        public fun isConnectedToInternet(context: Context): Boolean {
            val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Logger.println("Internet - " + "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Logger.println("Internet - " + "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Logger.println("Internet - " + "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
            return false
        }

        private fun hideSystemUI(window: Window) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE)
        }

        private fun showSystemUI(window: Window) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

        fun getAgoTime(datetime: String): String {
            return DateUtils.getRelativeTimeSpanString(
                    getTimeInMillis(datetime),
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS
            ) as String
        }

        private fun getTimeInMillis(givenDateString: String): Long {
            var givenDateString: String? = givenDateString
            if (givenDateString != null) {
                givenDateString = givenDateString.replace("Z", "+0000")
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                try {
                    val mDate = sdf.parse(givenDateString)
                    return mDate.time
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
            return 0
        }

        fun fireShareIntent(context: Context?, title: String, id: String, isLive: Boolean) {
            val sendIntent = Intent()
            val slug: String =
                if (isLive) title else
                    title.toLowerCase().replace("[^\\dA-Za-z ]".toRegex(), "").replace("\\s+".toRegex(), "-")
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, getSharingUrl(id, slug, isLive))
            sendIntent.type = "text/plain"
            context?.startActivity(Intent.createChooser(sendIntent, null))
        }


        fun getHeight(context: Context): Int {
            val displayMetrics = context.resources.displayMetrics
            val fullHeight = displayMetrics.heightPixels / displayMetrics.density
            return TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    fullHeight,
                    context.resources.displayMetrics
            )
                .toInt()
        }

        fun dpToPx(context: Context, dp: Int): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }

        fun updateWakeLock(mWindow: Window, enable: Boolean) {
            var enable = enable
            enable = true // Always true.
            if (enable) {
                mWindow.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                mWindow.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        fun isAutoRotateOn(context: Context): Boolean {
            return Settings.System.getInt(
                    context.contentResolver,
                    Settings.System.ACCELEROMETER_ROTATION,
                    0
            ) == 1
        }

        @SuppressLint("HardwareIds")
        fun getDeviceId(context: Context?): String? {
            return if (context != null) {
                try {
                    Settings.Secure.getString(context.contentResolver, "android_id")
                } catch (e: Exception) {
                    ""
                }
            } else ""
        }

        // Function to generate selected bitrate URL for VOD
        fun generateVodUrl(bitrate: String, filename: String): String {
            //https://androidvod.goonj.pk/JA-N2-10-06-21_baseline_144.m4v/index.m3u8?uid=4c8e4f006cb3453f&media_id=DrA3

            return Constants.VOD_URL + filename.split(".")[0] + (
                    if(bitrate == Constants.Companion.NewBitRates.BITRATE_DATA_SAVER) "_baseline_144.m4v"
                    else if(bitrate == Constants.Companion.NewBitRates.BITRATE_MEDIUM) "_main_360.m4v"
                    else "_main_480.m4v") + "/index.m3u8";
        }

        // Function to generate selected bitrate URL for live
        fun generateLiveUrl(selectedBitrate: String, url: String): String? {
            Logger.println("Selected Bitrates: "+selectedBitrate)
            return parseLiveUrl(url, selectedBitrate)
        }

        // Parse Live URL
        private fun parseLiveUrl(url: String, bitrate: String): String? {
            if (getChannelPrefix(url).isEmpty()) {
                return ""
            }
            return if (bitrate == Constants.Companion.NewBitRates.BITRATE_AUTO) getChannelPrefix(url) + ".m3u8" else if (bitrate == Constants.Companion.NewBitRates.BITRATE_DATA_SAVER) getChannelPrefix(
                    url
            ) + "_144p/index.m3u8" else if (bitrate == Constants.Companion.NewBitRates.BITRATE_MEDIUM) getChannelPrefix(
                    url
            ) + "_360p/index.m3u8" else if (bitrate == Constants.Companion.NewBitRates.BITRATE_HIGH) getChannelPrefix(
                    url
            ) + "_480p/index.m3u8" else getChannelPrefix(url) + ".m3u8"
        }

        private fun getChannelPrefix(url: String): String {
            return if (url.split("/").toTypedArray().size > 3) Constants.LIVE_URL + url.split("/")
                .toTypedArray()[3].split("_").toTypedArray()[0].split(".m3u8").toTypedArray()[0] else ""
        }

        //Function to get parameters of VOD url
        fun getVideoIdForVOD(link: String): String? {
            val linkSplit = link.split("/").toTypedArray()
            return if (linkSplit.size > 3) {
                if (linkSplit[3].split("_").toTypedArray().size > 1) linkSplit[3].split("_")
                    .toTypedArray()[0] else ""
            } else {
                ""
            }
        }

        //Function to get id of Live Urls
        fun getChannelNameFromUrl(link: String): String? {
            val linkSplit = link.split("/").toTypedArray()
            return if (linkSplit.size > 3) {
                if (linkSplit[4] != "") linkSplit[4].toLowerCase() else ""
            } else {
                ""
            }
        }

        fun shootReportingParams(
                context: Context?,
                vodId: String?,
                title: String?,
                isLive: Boolean,
                source: String?,
                durationInSec: Int,
                category: String?
        ) {
            val ssoId: String = DeviceInfo.getDeviceId(context)!!;
            val calendar = Calendar.getInstance()
            val arrayList: ArrayList<Params> = ArrayList<Params>()
            arrayList.add(Params("sso_id", ssoId))
            arrayList.add(Params("video_id", vodId))
            arrayList.add(Params("media_type", if (isLive) "live" else "vod"))
            arrayList.add(Params("source", if (isLive) title else source))
            arrayList.add(Params("carrier", "telenor"))
            arrayList.add(Params("app", "goonj"))
            arrayList.add(Params("duration", durationInSec))
            arrayList.add(Params("month", calendar[Calendar.MONTH]))
            arrayList.add(Params("year", calendar[Calendar.YEAR]))
            arrayList.add(Params("day", calendar[Calendar.DAY_OF_MONTH]))
            if (category != null) arrayList.add(Params("category", category)) else arrayList.add(
                    Params(
                            "category",
                            ""
                    )
            )
            val obj = getJSONObject(arrayList)
//        if (!isLive && durationInSec > 3) RestClient(
//            context,
//            Constants.REPORTING_URL,
//            RestClient.Method.POST,
//            obj,
//            null
//        ).executeReq()
        }

        fun getJSONObject(parameters: ArrayList<Params>): JSONObject {
            var jObj: JSONObject
            try {
                var stringer = JSONStringer().`object`()
                for (param in parameters) {
                    stringer = stringer.key(param.getKey())
                    if (param.getValue() != null) {
                        stringer = stringer.value(param.getValue())
                    } else if (param.getIntValue() !== -1) {
                        stringer = stringer.value(param.getIntValue())
                    } else {
                        stringer = stringer.value(param.getBoolValue())
                    }
                }
                stringer = stringer.endObject()
                jObj = JSONObject(stringer.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                jObj = JSONObject()
            }
            return jObj
        }

        fun getStringList(array: JSONArray): Array<String?>? {
            val str = arrayOfNulls<String>(array.length())
            for (j in 0 until array.length()) {
                try {
                    val topic = array.getString(j)
                    if (!topic.isEmpty()) str[j] = topic
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return str
        }

        @SuppressLint("DefaultLocale")
        fun getFormattedStringFromSec(sec: Int): String? {
            val millis = (sec * 1000).toLong()
            return String.format(
                    "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
                            TimeUnit.MILLISECONDS.toHours(
                                    millis
                            )
                    ),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(
                                    millis
                            )
                    )
            )
        }

        //set configs from firebase
        fun setConstants(`object`: FirebaseRemoteConfig) {
            Constants.CORONA_BANNER_VISIBILITY = `object`.getBoolean("corona_banner_visibility")
            Constants.CORONA_HOTLINE_IMAGE = `object`.getString("corona_hotline_image")
            Constants.FORCE_UPDATE_VERSION = `object`.getString("force_update_version").toInt()
            Constants.IN_FEED_AD_FREQ = `object`.getString("in_feed_ad_freq").toInt()
            Constants.HE_URL = `object`.getString("he_url")
            Constants.VOD_ADTAG_SOURCE = `object`.getString("vod_adtag_source")
            Constants.IS_PAYWALL_ENABLED = `object`.getBoolean("is_paywall_enabled")
            Constants.CATEGORIES_STRING_JSON = `object`.getString("v2_home_categories")
            Constants.CATEGORIES_CHANNEL_JSON = `object`.getString("v2_channel_categories")
            Constants.COMEDY_CATEGORIES_STRING_JSON = `object`.getString("comedy_categories_json")
            Constants.COMEDY_BASE_URL = `object`.getString("comedy_base_url")
            Constants.COMEDY_API_KEY = `object`.getString("comedy_api_key")
            Constants.IS_LIVE_PRE_ROLL_ENABLED = `object`.getBoolean("is_live_pre_roll_enabled")
            Constants.IN_FEED_AD_FREQ = `object`.getString("in_feed_ad_freq").toInt()
            Constants.IS_PAYWALL_ENABLED = `object`.getBoolean("is_paywall_enabled")
            Constants.CHECK_SUBS_STATUS_INTERVAL = `object`.getLong("check_subs_status_interval")
            Constants.CHECK_SMALL_SUBS_STATUS_INTERVAL = `object`.getLong("check_small_subs_status_interval")
            Constants.LIVE_URL = `object`.getString("live_url")
            Constants.PREROLL_VOD_FREQ = `object`.getString("preroll_vod_freq").toInt()
            Constants.PREROLL_LIVE_FREQ = `object`.getString("preroll_live_freq").toInt()
            Constants.VOD_URL = `object`.getString("vod_url")
            Constants.VOD_POSTFIX_URL = `object`.getString("vod_postfix_url")
            Constants.API_BASE_URL = `object`.getString("api_url")
            Constants.PAYWALL_BASE_URL = `object`.getString("paywall_base_url")

            //Constants.PAYWALL_BASE_URL = "http://staging.api.goonj.pk/v2/";
            //Constants.PAYWALL_BASE_URL = "http://3.126.102.117:5000/";
            //Constants.API_BASE_URL = "http://staging.api.goonj.pk/v2/";
            Constants.CONTACT_US_NUMBER = `object`.getString("contact_us_number")
            Constants.CDN_STATIC_URL = `object`.getString("cdn_static")
            Constants.TERMS_URL = `object`.getString("terms_url")
            Constants.PRIVACY_POLICY_URL = `object`.getString("privacy_url")
            Constants.UPDATE_AVAILABLE_IMAGE = `object`.getString("update_available_image")
            Constants.FORCE_UPDATE = `object`.getBoolean("force_update")
            Constants.IS_ONLY_MESSAGE = `object`.getBoolean("is_only_message")
            Constants.IS_UPDATE_AVAILABLE = `object`.getBoolean("is_update_available")
            Constants.UPDATE_MESSAGE = `object`.getString("update_message")
            Constants.LATEST_VERSION = `object`.getString("latest_version").toInt()
            Constants.REPORTING_URL = `object`.getString("reporting_url")
            Constants.PRE_ROLL_TAG = `object`.getString("preroll_tag")
            Constants.FETCH_TOTAL_LIKES_URL = `object`.getString("fetch_total_likes_url")
            Constants.CHECK_LIKE_URL = `object`.getString("check_like_url")
            Constants.LIKE_VIDEO_URL = `object`.getString("like_video_url")
            Constants.DOUBLE_CLICK_300X250_AD_UNIT_ID =
                `object`.getString("double_click_300x250_ad_unit_id")
            Constants.DOUBLE_CLICK_320X50_AD_UNIT_ID =
                `object`.getString("double_click_320x50_ad_unit_id")
            Constants.LANDING_PAGE_320x100_AD_UNIT_ID =
                `object`.getString("landing_page_320x100_ad_unit_id")
            Constants.SECURITY_ACL = `object`.getString("security_acl")
            Constants.SECURITY_TOKEN_NAME = `object`.getString("security_token_name")
            Constants.SECURITY_KEY = `object`.getString("security_key")
            Constants.SECURITY_WINDOW = `object`.getString("security_window")
            Constants.DEFAULT_AKAMAI_TOKEN = `object`.getString("akamai_default_token")
            try {
                val rootArray = JSONArray(`object`.getString("exclude_preroll_channels"))
                for (i in 0 until rootArray.length()) {
                    Constants.EXCLUDE_PRE_ROLL_CHANNELS.plus(rootArray.getString(i))
                }
                val flagged = JSONArray(`object`.getString("flagged_keywords"))
                for (i in 0 until flagged.length()) {
                    Constants.FLAGGED_KEYWORDS.plus(flagged.getString(i))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //generate url for sharing
        fun getSharingUrl(id: String, slug: String, isLive: Boolean): String? {
            val url: String
            url = if (isLive) {
                Constants.GOONJ_URL + Constants.CHANNEL + slug
            } else {
                Constants.GOONJ_URL + id + "_" + slug
            }
            return "https://${url.replace(" ", "%20")}"
        }

        fun getNetworkIndex(networkList: List<Channel>, id: String?): Int {
            var index = 0
            for (i in networkList.indices) {
                if (networkList[i].getId().equals(id)) {
                    index = i
                }
            }
            return index
        }

        fun moveToSplashActivity(context: Context) {
            context.startActivity(Intent(context, SplashActivity::class.java))
        }

//    fun moveToLiveFragment(action: String?, model: HomeModel, context: Context) {
//        val intent = Intent(context, WelcomeActivity::class.java)
//        intent.putExtra("IS_LIVE_LINK", "true")
//        intent.putExtra("ID", model.getId())
//        intent.putExtra("LINK", model.getHlsLink())
//        if (action != null) {
//            intent.putExtra("action", action)
//        }
//        context.startActivity(intent)
//    }


        fun changeHttpsToHttp() {
            Constants.VOD_URL = Constants.VOD_URL!!.replace("https://", "http://")
            Constants.CDN_STATIC_URL = Constants.CDN_STATIC_URL!!.replace("https://", "http://")
            Constants.API_BASE_URL = Constants.API_BASE_URL!!.replace("https://", "http://")
            Constants.VOD_URL = Constants.VOD_URL!!.replace("https://", "http://")
            Constants.LIVE_URL = Constants.LIVE_URL!!.replace("https://", "http://")
            Constants.PRIVACY_POLICY_URL = Constants.PRIVACY_POLICY_URL!!.replace("https://", "http://")
        }

        fun getNextNamazTime(response: String): String {
            val rootObj = JSONObject(response);

            lateinit var imSak: String;

            if(rootObj.getInt("code") == 200){
                val timesObj: JSONObject = rootObj.getJSONObject("results").getJSONArray("datetime").getJSONObject(0).getJSONObject("times");

                imSak = "Imsak " + timesObj.getString("Imsak");
                val sunrise = "Sunrise " + timesObj.getString("Sunrise");
                val fajr = "Fajr " + timesObj.getString("Fajr");
                val dhuhr = "Dhuhr " + timesObj.getString("Dhuhr");
                val asr = "Asr " + timesObj.getString("Asr");
                val sunset = "Sunset " + timesObj.getString("Sunset");
                val maghrib = "Maghrib " + timesObj.getString("Maghrib");
                val isha = "Isha " + timesObj.getString("Isha");

                val timesArray = arrayOf(imSak, sunrise, fajr, dhuhr, asr, sunset, maghrib, isha);

                for (i in timesArray.indices){
                    Logger.println("------------"+timesArray[i]);
                    if(hourOf(timesArray[i]).timeInMillis > Calendar.getInstance().timeInMillis){
                        val namazName = timesArray[i].split(" ")[0];
                        val date: Date = hourOf(timesArray[i]).time;
                        val sdf = SimpleDateFormat("hh:mm aa")
                        return namazName +" " + sdf.format(date);
                    }
                }
            }

            return imSak;
        }

        private fun hourOf(time: String): Calendar {
            val mCal: Calendar = Calendar.getInstance();
            mCal.set(Calendar.HOUR_OF_DAY, time.split(' ')[1].split(":")[0].toIntOrNull()!!);
            mCal.set(Calendar.MINUTE, time.split(' ')[1].split(":")[1].toIntOrNull()!!);
            return mCal;
        }

        fun getCategoryWiseChannel(json: String?, category: String, context: Context?): ArrayList<Channel> {
            var list: ArrayList<Channel>;
            if(json != null){
                list = JSONParser.getLiveChannels(json);
            }else{
                list = GoonjPrefs(context).getChannels();
            }

            return list!!.filter { s -> s.getCategory() == category } as ArrayList<Channel>
        }

        fun getNumberFormat(number: String): String{
            var numberString = ""
            numberString = if (Math.abs(BigDecimal(number).intValueExact() / 1000000) > 1) {
                (BigDecimal(number).intValueExact() / 1000000).toString() + "M"
            } else if (Math.abs(BigDecimal(number).intValueExact() / 1000) > 1) {
                (BigDecimal(number).intValueExact() / 1000).toString() + "K"
            } else {
                number
            }

            return numberString;
        }
    }
}