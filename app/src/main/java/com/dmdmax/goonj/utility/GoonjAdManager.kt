package com.dmdmax.goonj.utility

import android.content.Context
import com.dmdmax.goonj.base.BaseActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class GoonjAdManager {

    private var mInterstitialAd: InterstitialAd? = null;
    private var mContext: Context? = null;

    fun loadInterstitialAd(context: Context){
        this.mContext = context;
        var adRequest = AdRequest.Builder().build();

        InterstitialAd.load(context,"ca-app-pub-8209802528239642/7297039195", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Logger.println("InterstitialAd - onAdFailedToLoad" + adError?.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Logger.println("InterstitialAd - onAdLoaded Ad was loaded.");
                mInterstitialAd = interstitialAd
                displayInterstitialAd();
            }
        });
    }

    fun displayInterstitialAd(){
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Logger.println("Ad was dismissed")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Logger.println("Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                Logger.println("Ad showed fullscreen content.")
                mInterstitialAd = null;
            }
        }

        if (mInterstitialAd != null) {
            mInterstitialAd?.show(mContext as BaseActivity);
        } else {
            Logger.println("The interstitial ad wasn't ready yet.")
        }
    }
}