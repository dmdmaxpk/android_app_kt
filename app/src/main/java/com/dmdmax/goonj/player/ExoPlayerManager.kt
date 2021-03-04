package com.dmdmax.goonj.player

import android.content.Context
import android.net.Uri
import android.os.Handler
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.base.BaseApplication
import com.dmdmax.goonj.models.MediaModel
import com.dmdmax.goonj.screens.fragments.ChannelsFragment
import com.dmdmax.goonj.screens.fragments.ComedyFragment
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Utility
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory
import com.google.android.exoplayer2.source.hls.HlsDataSourceFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util


class ExoPlayerManager {

    private lateinit var mPlayer: SimpleExoPlayer;
    private lateinit var mContext: Context;
    private lateinit var mAdsLoader: ImaAdsLoader;
    private lateinit var mPrefs: GoonjPrefs;

    fun init(context: Context) {
        mPlayer = ExoPlayerFactory.newSimpleInstance(
            context,
            DefaultRenderersFactory(context),
            DefaultTrackSelector(),
            DefaultLoadControl()
        )
        mPrefs = GoonjPrefs(context);
        this.mContext = context;
    }

    fun playMedia(mediaModel: MediaModel){
        Utility.updateWakeLock((mContext as BaseActivity).window, true)

        BaseApplication.getInstance().expireCookies()
        BaseApplication.getInstance().setCookies()

        var url: String
        if (!mediaModel.isLive()) url =
            mediaModel.getUrl() + "?uid=" + Utility.getDeviceId(mContext)  + "&media_id=" + mediaModel.getId() else {
            try {
                url =
                    mediaModel.getUrl() + "?user_id=" + mPrefs.getUserId() + "&uid=" + Utility.getDeviceId(mContext) + "&media_id=" + mediaModel.getId()
                val commands = arrayOf(
                    "-k",
                    Constants.SECURITY_KEY,
                    "-w",
                    Constants.SECURITY_WINDOW,
                    "-n",
                    Constants.SECURITY_TOKEN_NAME,
                    "-a",
                    Constants.SECURITY_ACL
                )
                val token: String = AkamaiToken.main(commands)
                url = "$url&$token"
            } catch (e: java.lang.Exception) {
                url = ""
                e.printStackTrace()
            }
        }
        url = url.replace(" ".toRegex(), "%20")
        Logger.println("Stream URL: $url")

        val msisdn = if (mPrefs.getMsisdn(ChannelsFragment.SLUG) != null) mPrefs.getMsisdn(ChannelsFragment.SLUG) else if (mPrefs.getMsisdn(ComedyFragment.SLUG) != null) mPrefs.getMsisdn(ComedyFragment.SLUG) else "null"
        val userAgent = "msisdn_" + msisdn + "_ua:goonjlive"
        val hlsDataSourceFactory: HlsDataSourceFactory = DefaultHlsDataSourceFactory(DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, userAgent), DefaultBandwidthMeter()))
        val mediaSource: MediaSource? =
            HlsMediaSource.Factory(hlsDataSourceFactory).createMediaSource(
                Uri.parse(url)
            )
        if (mediaSource != null) {
            if (!mediaModel.isLive()) {
                mPlayer.prepare(mediaSource, true, false)
            } else {
                mPlayer.prepare(mediaSource, false, true)
            }
        }
    }

    fun getPlayer(): SimpleExoPlayer {
        return mPlayer;
    }

    fun displayBufferLayout(){

    }

    fun displayNonBufferLayout(){

    }

    fun pause(){
        mPlayer.playWhenReady = false;
    }

    fun resume(){
        mPlayer.playWhenReady = true;
    }


    private fun buildMediaSource(uri: Uri, adTag: String?): MediaSource? {
        val msisdn = if (mPrefs.getMsisdn(ChannelsFragment.SLUG) != null) mPrefs.getMsisdn(
            ChannelsFragment.SLUG
        ) else if (mPrefs.getMsisdn(ComedyFragment.SLUG) != null) mPrefs.getMsisdn(ComedyFragment.SLUG) else "null"
        val userAgent = "msisdn_" + msisdn + "_ua:goonjvod"

        val mHlsDsFactory: HlsDataSourceFactory = DefaultHlsDataSourceFactory(
            DefaultDataSourceFactory(
                mContext, Util.getUserAgent(
                    mContext,
                    userAgent
                ), DefaultBandwidthMeter()
            )
        );

        if (uri.lastPathSegment != null && (uri.lastPathSegment!!.contains("mp3") || uri.lastPathSegment!!.contains("mp4"))) {
            return ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent)).createMediaSource(
                uri
            )
        } else if (uri.lastPathSegment != null && uri.lastPathSegment!!.contains("m3u8")) {
            if (adTag == null) {
                return HlsMediaSource.Factory(mHlsDsFactory).createMediaSource(uri)
            } else {
                try {
                    val imaSdkSettings = ImaSdkFactory.getInstance().createImaSdkSettings()
                    mAdsLoader = ImaAdsLoader.Builder(mContext).setImaSdkSettings(imaSdkSettings).buildForAdTag(Uri.parse(adTag))
                    //return new AdsMediaSource(new HlsMediaSource.Factory(mHlsDsFactory).createMediaSource(uri), mHlsDsFactory, loader, playerView.getOverlayFrameLayout());
                    return HlsMediaSource.Factory(mHlsDsFactory).createMediaSource(uri)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

}