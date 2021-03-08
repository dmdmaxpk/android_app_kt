package com.dmdmax.goonj.player

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import com.dmdmax.goonj.R
import com.dmdmax.goonj.adapters.BitrateAdapter
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.base.BaseApplication
import com.dmdmax.goonj.models.BitRatesModel
import com.dmdmax.goonj.models.MediaModel
import com.dmdmax.goonj.screens.fragments.ChannelsFragment
import com.dmdmax.goonj.screens.fragments.ComedyFragment
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Utility
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory
import com.google.android.exoplayer2.source.hls.HlsDataSourceFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util


class ExoPlayerManager: View.OnClickListener {

    private lateinit var mPlayer: SimpleExoPlayer;
    private lateinit var mContext: Context;
    private lateinit var mAdsLoader: ImaAdsLoader;
    private lateinit var mPrefs: GoonjPrefs;

    private var isDefaultVolumeOn = true;
    private var isDefaultBitrateGridOn = false;
    private var mCurrentVolume: Float = 0.0f;
    private lateinit var mPlayerView: PlayerView;

    private lateinit var mExoPause: ImageButton;
    private lateinit var mExoPlay: ImageButton;
    private lateinit var mExoBuffering: ProgressBar;
    private lateinit var mExoVolume: ImageButton;
    private lateinit var mExoSettings: ImageButton;

    private lateinit var mMediaModel: MediaModel;

    private lateinit var mExoBitrateLayout: LinearLayout;
    private lateinit var mExoBitrateGrid: GridView;

    private lateinit var mBitrateList: ArrayList<BitRatesModel>;

    fun init(context: Context, playerView: PlayerView) {
        mPlayer = ExoPlayerFactory.newSimpleInstance(
                context,
                DefaultRenderersFactory(context),
                DefaultTrackSelector(),
                DefaultLoadControl()
        )
        this.mPlayerView = playerView;
        mPrefs = GoonjPrefs(context);
        this.mContext = context;

        this.mPlayerView.player = getPlayer();

        mExoPause = this.mPlayerView.findViewById(R.id.exo_pause);
        mExoPlay = this.mPlayerView.findViewById(R.id.exo_play);
        mExoBuffering = this.mPlayerView.findViewById(R.id.exo_buffering);
        mExoVolume = this.mPlayerView.findViewById(R.id.exo_volume);
        mExoSettings = this.mPlayerView.findViewById(R.id.exo_settings);

        mExoBitrateLayout = this.mPlayerView.findViewById(R.id.exo_bitrate_layout);
        mExoBitrateGrid = this.mPlayerView.findViewById(R.id.exo_bitrate_grid);

        setIconsOnPlayers();

        addListeners();
    }

    fun setIconsOnPlayers(){
        if(isDefaultVolumeOn){
            mExoVolume.setImageResource(R.drawable.volume_high)
        }else{
            mExoVolume.setImageResource(R.drawable.volume_mute)
        }

        mBitrateList = Constants.getBitrates(mContext);
        val mAdapter = BitrateAdapter(mBitrateList, mContext);
        mExoBitrateGrid.adapter = mAdapter;
        mExoBitrateGrid.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mBitrateList.get(mAdapter.getCurrentSelected()).setSelected(false)
                mBitrateList.get(position).setSelected(true)
                mAdapter.notifyDataSetChanged()
                Handler().postDelayed({ mExoBitrateLayout.visibility = View.GONE; isDefaultBitrateGridOn = true }, 700);

                // Play media
                if (mMediaModel.isLive()) {
                    mMediaModel.setUrl(Utility.generateLiveUrl(mBitrateList.get(position-1).getBitrate()!!, mMediaModel.getUrl()!!));
                } else {
                    mMediaModel.setUrl(Utility.generateVodUrl(mBitrateList.get(position-1).getBitrate()!!, Constants.getVodStreamingLink(mMediaModel.getFilename()), Constants.getExtension(mMediaModel.getFilename())));
                }
                playMedia(mMediaModel);
            }
        });
    }

    fun playMedia(mediaModel: MediaModel){
        this.mMediaModel = mediaModel;
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

        this.mPlayerView.player.playWhenReady = true;
        this.mPlayerView.useController = false;
    }

    fun getPlayer(): SimpleExoPlayer {
        return mPlayer;
    }

    fun pause(){
        mPlayer.playWhenReady = false;
    }

    fun resume(){
        mPlayer.playWhenReady = true;
    }

    fun mute(){
        mCurrentVolume = mPlayer.volume;
        mPlayer.volume = 0f;
        mExoVolume.setImageResource(R.drawable.volume_mute)
        isDefaultVolumeOn = false;
    }

    fun unMute(){
        mPlayer.volume = mCurrentVolume;
        mExoVolume.setImageResource(R.drawable.volume_high)
        isDefaultVolumeOn = true;
    }

    fun addListeners() {
        mExoVolume.setOnClickListener(this);
        mExoSettings.setOnClickListener(this);

        this.mPlayerView.player.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)

                // Loading Time
                if (playbackState == Player.STATE_READY) {
                    mPlayerView.useController = true;
                    Logger.println("STATE_READY");
                    if (mPlayerView.player.playWhenReady) {
                        // Currently playing
                        mExoPause.visibility = View.VISIBLE;
                        mExoPlay.visibility = View.GONE;
                    } else {
                        // Stopped
                        mExoPause.visibility = View.GONE;
                        mExoPlay.visibility = View.VISIBLE;
                    }
                    mExoBuffering.visibility = View.GONE;
                } else if (playbackState == Player.STATE_BUFFERING) {
                    Logger.println("STATE_BUFFERING");
                    mExoPause.visibility = View.GONE;
                    mExoPlay.visibility = View.GONE;
                    mExoBuffering.visibility = View.VISIBLE;
                } else {
                    Logger.println("ELSE");
                    mExoPause.visibility = View.GONE;
                    mExoPlay.visibility = View.VISIBLE;
                    mExoBuffering.visibility = View.GONE;
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v){
            mExoVolume -> {
                if (isDefaultVolumeOn) {
                    mute();
                } else {
                    unMute();
                }
            }

            mExoSettings -> {
                if (isDefaultBitrateGridOn) {
                    mExoBitrateLayout.visibility = View.VISIBLE;
                    isDefaultBitrateGridOn = false;
                } else {
                    mExoBitrateLayout.visibility = View.GONE;
                    isDefaultBitrateGridOn = true;
                }
            }
        }
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