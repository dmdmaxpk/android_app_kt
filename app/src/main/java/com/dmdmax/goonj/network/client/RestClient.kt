package com.dmdmax.goonj.network.client


import android.content.Context
import com.android.volley.*
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.StringRequest
import com.dmdmax.goonj.models.Params
import com.dmdmax.goonj.screens.fragments.paywall.PaywallBinjeeFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Toaster
import com.dmdmax.goonj.utility.Utility
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*
import javax.net.ssl.SSLHandshakeException
import kotlin.collections.ArrayList

class RestClient {

    private var mBody: JSONObject? = null;
    private var mLink: String;
    private var mMethod: String;
    private var mContext: Context;
    private var mPrefs: GoonjPrefs;
    private var mList: ArrayList<Params>?;
    private var mListener: NetworkOperationListener;

    private var mCategory: String? = null;


    constructor(context: Context, url: String, method: String, list: ArrayList<Params>?, listener: NetworkOperationListener) {
        this.mList = list;
        Logger.println("RestClient - $method - URL - $url");

        if(mList != null){
            this.mBody = Utility.getJSONObject(mList!!);
            Logger.println("RestClient - Body - $mBody");
        }

        this.mContext = context;
        this.mListener = listener
        this.mLink = url;
        this.mMethod = method;
        mPrefs = GoonjPrefs(context);


        if (Constants.IS_SSL_EXCEPTION) {
            Utility.changeHttpsToHttp();
            mLink = url!!.replace("https://", "http://");
        }
    }

    companion object {
        object Method {
            const val GET = "GET"
            const val POST = "POST"
            const val PUT = "PUT"
        }
    }

    public fun exec(){
        if (Utility.isConnectedToInternet(mContext)) {
            getResponse(null)
        } else {
            retryDialog();
        }
    }

    public fun exec(category: String?, formData: Map<String, String>?) {
        if (Utility.isConnectedToInternet(mContext)) {
            this.mCategory = category;
            if(formData == null){
                getResponse(category);
            }else{
                getFormBodyResponse(category, formData)
            }
        } else {
            retryDialog()
        }
    }

    public fun execComedy() {
        if (Utility.isConnectedToInternet(mContext)) {
            getResponse(PaywallComedyFragment.SLUG)
        } else {
            retryDialog()
        }
    }

    private fun getAuthHeaders(category: String?): HashMap<String, String> {
        Logger.println("AUTH HEADERS:  $category");
        val headers = HashMap<String, String>()
        if (category != null && category == PaywallBinjeeFragment.SLUG) {
            val creds = String.format("%s:%s", Constants.Companion.EndPoints.BINJEE_USERNAME,Constants.Companion.EndPoints.BINJEE_PASSWORD)
            //val auth = "Basic " + toBase64(creds);
            val auth = "Basic YiFuajMzMHIhZyFuQEk1OmIhbmozM2F0MG4zdHcwdGhyMzM=";
            Logger.println("AUTH: $auth");
            headers["Authorization"] = auth;
        }else if (category != null && category == PaywallComedyFragment.SLUG) {
            headers["API-KEY"] = Constants.COMEDY_API_KEY!!
        } else {
            headers["Authorization"] = "Bearer " + mPrefs!!.getAccessToken()
        }
        return headers
    }


    private fun getFormBodyResponse(category: String?, formData: Map<String, String>) {
        val jRequest: StringRequest = object : StringRequest(Method.POST, mLink, StringSuccessListener(), ErrorListener()) {
            override fun getParams(): Map<String, String> {
                return formData
            }

            override fun getHeaders(): Map<String, String> {
                if (category != null && category == PaywallComedyFragment.SLUG) {
                    return getAuthHeaders(PaywallComedyFragment.SLUG)
                }else if(category != null && category == PaywallBinjeeFragment.SLUG){
                    return getAuthHeaders(PaywallBinjeeFragment.SLUG)
                }else{
                    return getAuthHeaders(category)
                }
            }
        }
        addRequest(jRequest);
    }

    private fun getResponse(category: String?) {
        try {
            Logger.println("URL: $mLink")
            if (Method.GET.equals(mMethod)) {
                val jRequest: StringRequest = object : StringRequest(Method.GET, mLink, StringSuccessListener(), ErrorListener()) {
                    override fun getHeaders(): Map<String, String> {
                        if (category != null && category == PaywallComedyFragment.SLUG) {
                            return getAuthHeaders(PaywallComedyFragment.SLUG)
                        }else if(category != null && category == PaywallBinjeeFragment.SLUG){
                            return getAuthHeaders(PaywallBinjeeFragment.SLUG)
                        }
                        return getAuthHeaders(category)
                    }
                }
                addRequest(jRequest);
            } else if (Method.POST.equals(mMethod)) {
                val jRequest: JsonRequest<*> = object : JsonObjectRequest(Method.POST, mLink, mBody, JsonSuccessListener(), ErrorListener()) {
                    override fun getHeaders(): Map<String, String> {
                        if (category != null && category == PaywallComedyFragment.SLUG) {
                            return getAuthHeaders(PaywallComedyFragment.SLUG)
                        }else if(category != null && category == PaywallBinjeeFragment.SLUG){
                            return getAuthHeaders(PaywallBinjeeFragment.SLUG)
                        }
                        return getAuthHeaders(category)
                    }
                }
                addRequest(jRequest);
            } else if (Method.PUT.equals(mMethod)) {
                val jRequest: JsonRequest<*> = object : JsonObjectRequest(Method.PUT, mLink, mBody, JsonSuccessListener(), ErrorListener()) {
                    override fun getHeaders(): Map<String, String> {
                        if (category != null && category == PaywallComedyFragment.SLUG) {
                            return getAuthHeaders(PaywallComedyFragment.SLUG)
                        }else if(category != null && category == PaywallBinjeeFragment.SLUG){
                            return getAuthHeaders(PaywallBinjeeFragment.SLUG)
                        }
                        return getAuthHeaders(category)
                    }
                }
                addRequest(jRequest);
            }
        } catch (e: Exception) {
            Logger.println("AUTH FAILURE EXCEPTION: " + e.message)
            e.printStackTrace()
        }
    }

    private inner class JsonSuccessListener : Response.Listener<JSONObject> {
        override fun onResponse(response: JSONObject) {
            try {
                if (response.has("code") && (response.getInt("code") == 401 || response.getInt("code") == 403)) {
                    getRefreshTokenAndExecRequest()
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (mListener != null) mListener.onSuccess(response.toString())
        }
    }

    private inner class StringSuccessListener : Response.Listener<String> {
        override fun onResponse(response: String) {
            try {
                val res = JSONObject(response)
                if (res.has("code") && (res.getInt("code") == 401 || res.getInt("code") == 403)) {
                    getRefreshTokenAndExecRequest()
                    return
                }
            } catch (e: Exception) {}
            if (mListener != null) mListener.onSuccess(response)
        }
    }

    private fun getRefreshTokenAndExecRequest() {
        val params: ArrayList<Params> = ArrayList<Params>()
        params.add(Params("token", mPrefs!!.getRefreshToken()))
        val jRequest: JsonRequest<*> = JsonObjectRequest(Request.Method.POST, Constants.PAYWALL_BASE_URL + Constants.Companion.EndPoints.REFRESH_TOKEN, Utility.getJSONObject(params), { rootObj ->
            try {
                Logger.println("NEW ACCESS TOKEN FETCHED: " + rootObj.getString("access_token"))
                mPrefs!!.setAccessToken(rootObj.getString("access_token"))
                mPrefs!!.setRefreshToken(rootObj.getString("refresh_token"))
                RestClient(mContext, mLink, mMethod, mList, mListener).exec();
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }) { error ->
            Logger.println("REFRESH API CAL FAILED")
            error.printStackTrace()
        }
        addRequest(jRequest)
    }

    private inner class ErrorListener : Response.ErrorListener {
        override fun onErrorResponse(err: VolleyError) {
            if (mCategory == null && err.networkResponse != null && err.networkResponse.statusCode == 401) {
                // Un-Authorized
                Logger.println("Un-Authorized: $mLink")
                getRefreshTokenAndExecRequest()
            } else if (err.networkResponse != null && err.networkResponse.statusCode == 403) {
                // Forbidden
                Logger.println("Forbidden: $mLink")
                getRefreshTokenAndExecRequest()
            } else if (mListener != null) {
                Logger.println("onFailed: " + err.message)
                if (err.cause is SSLHandshakeException) {
                    Constants.IS_SSL_EXCEPTION = true
                    RestClient(mContext, mLink, mMethod, mList, mListener).exec();
                } else {
                    if (err.networkResponse != null) {
                        if (err.networkResponse.statusCode == 504) {
                            //OutOfCountryBlockDialog()
                        }
                        mListener.onFailed(err.networkResponse.statusCode, err.message)
                    } else {
                        mListener.onFailed(0, err.message)
                    }
                }
            }
        }
    }

    private fun retryDialog(){
        Toaster.printToast(mContext, "No network found!");
    }

    /*private fun OutOfCountryBlockDialog() {
        Constants.OutOfCountryContext = context
        val intent = Intent(context, GeoRestrictedActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context!!.startActivity(intent)
    }*/

    private fun <T> addRequest(request: Request<T>){
        VolleySingleton.instance.getInstance(mContext).addToRequestQueue(request);
    }
}