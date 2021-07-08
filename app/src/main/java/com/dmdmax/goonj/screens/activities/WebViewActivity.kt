package com.dmdmax.goonj.screens.activities

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.screens.fragments.paywall.PaywallBinjeeFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger

class WebViewActivity : BaseActivity() {
    var webView: WebView? = null
    var page: String? = null
    var paywallSource: String? = null
    var pb: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        init()
        if (intent.extras != null) {
            val bundle = intent.extras
            page = bundle!!.getString("page")
            paywallSource = bundle.getString("slug")
            loadLink(page)
        }
    }

    private fun loadLink(page: String?) {
        if (page == "terms") {
            startWebView(Constants.TERMS_URL)
            EventManager.getInstance(this).fireEvent("Terms_And_Condition${EventManager.Events.VIEW}");
        }
        if (page == "privacy-policy") {
            if(paywallSource != null && paywallSource.equals(PaywallBinjeeFragment.SLUG)){
                // Go to binjee privacy policy
                startWebView("https://goonj.binjee.com/privacy")
            }else{
                startWebView(Constants.PRIVACY_POLICY_URL)
            }

            EventManager.getInstance(this).fireEvent("${paywallSource?.capitalize()}_Privacy_Policy${EventManager.Events.VIEW}");
        }
    }

    private fun init() {
        webView = findViewById(R.id.termsAndPrivacyPolicy)
        pb = findViewById(R.id.progressBar)
    }

    private fun startWebView(url: String?) {
        Logger.println("URL: $url")
        webView!!.clearHistory()
        webView!!.clearCache(true)
        webView!!.webChromeClient = WebChromeClient()
        webView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                pb!!.visibility = View.GONE
                webView!!.visibility = View.VISIBLE
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                Toast.makeText(
                    this@WebViewActivity,
                    "Error:$description",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        val settings = webView!!.settings
        settings.domStorageEnabled = true
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        webView!!.loadUrl(url)
    }
}