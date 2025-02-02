package com.dmdmax.goonj.screens.implements

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.adapters.BottomMenuChannelAdapter
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.MediaModel
import com.dmdmax.goonj.models.Params
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.player.ExoPlayerManager
import com.dmdmax.goonj.screens.activities.PlayerActivity
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.views.BottomMenuLiveTvView
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Utility
import java.util.*


class BottomMenuLiveTvImpl: BaseObservableView<BottomMenuLiveTvView.Listener>, BottomMenuLiveTvView, View.OnClickListener {

    private lateinit var mPlayerMainLayout: LinearLayout;
    private lateinit var mBelowPlayerLayout: LinearLayout;
    private lateinit var mProgressBar: ProgressBar;
    private lateinit var mDummy: ImageView;

    private lateinit var mChannelTitle: TextView;
    private lateinit var mHeader: FrameLayout;
    private lateinit var mMainPlayerView: FrameLayout;
    private lateinit var mPlayerManager: ExoPlayerManager;
    private lateinit var mPlayer: com.google.android.exoplayer2.ui.PlayerView;
    private lateinit var mPrefs: GoonjPrefs;

    private lateinit var mShare: LinearLayout;
    private lateinit var mRecommendedLiveChannels: RecyclerView;
    private lateinit var mList: ArrayList<Channel>;

    private var mCurrentChannel: Channel? = null;

    private lateinit var mNetworkStatusTextView: TextView;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.fragment_bottom_live, parent, false));
    }

    override fun initialize() {
        mNetworkStatusTextView = findViewById(R.id.network_status);
        mMainPlayerView = findViewById(R.id.main_player_view);
        mPlayerMainLayout = findViewById(R.id.player_main_layout);
        mBelowPlayerLayout = findViewById(R.id.below_player_layout);
        mProgressBar = findViewById(R.id.progress_bar);

        mChannelTitle = findViewById(R.id.channel_title);
        mShare = findViewById(R.id.share);
        mHeader = findViewById(R.id.header);
        mHeader.visibility = View.GONE;
        mRecommendedLiveChannels = findViewById(R.id.recommended_live_channels);

        setRecyclerView(mRecommendedLiveChannels)
        mShare.setOnClickListener(this);
        mPlayer = findViewById(R.id.video_view);

        mDummy = findViewById(R.id.dummy);
        mPrefs = GoonjPrefs(getContext());

        // init player
        mPlayerManager = ExoPlayerManager();
        mPlayerManager.init(getContext(), mPlayer);
        mPlayerManager.hideControllers();

        mList = getPrefs().getChannels();

        mDummy.setOnClickListener {
            play(mList[0]);
        }

        // Set listener
        mRecommendedLiveChannels.adapter = BottomMenuChannelAdapter(mList, getContext(), object : BottomMenuChannelAdapter.OnItemClickListener {
            override fun onClick(channel: Channel, position: Int) {
                play(channel);
            }
        })


        if(getPrefs().getSubscriptionStatus(PaywallGoonjFragment.SLUG) == PaymentHelper.Companion.PaymentStatus.STATUS_BILLED){
            mPlayerMainLayout.visibility = View.VISIBLE
            mProgressBar.visibility = View.GONE


            // Default play first channel
            if(mList.size > 0){
                play((mList.get(0)));
            }else{
                Logger.println("List size is zero")
            }
        }
        else{
            mPlayerMainLayout.visibility = View.GONE
            mProgressBar.visibility = View.VISIBLE

            if(getPrefs().getMsisdn(PaywallGoonjFragment.SLUG) != null && getPrefs().getMsisdn(PaywallGoonjFragment.SLUG)!!.isNotEmpty()){
                PaymentHelper(getContext(), PaymentHelper.PAYMENT_TELENOR).checkBillingStatus((getPrefs().getMsisdn(PaywallGoonjFragment.SLUG))!!, object: PaymentHelper.BillingStatusCheckListener{
                    override fun onStatus(code: Int, status: String) {
                        if(getPrefs().getSubscriptionStatus(PaywallGoonjFragment.SLUG) == PaymentHelper.Companion.PaymentStatus.STATUS_BILLED){
                            mPlayerMainLayout.visibility = View.VISIBLE
                            mProgressBar.visibility = View.GONE
                            mDummy.visibility = View.GONE;

                            // Default play first channel
                            if(mList.size > 0){
                                play((mList.get(0)));
                            }else{
                                Logger.println("List size is zero")
                            }

                        }else{
                            mPlayerMainLayout.visibility = View.VISIBLE
                            mProgressBar.visibility = View.GONE
                            mDummy.visibility = View.VISIBLE;
                        }
                    }

                })
            }
            else{
                mPlayerMainLayout.visibility = View.VISIBLE;
                mProgressBar.visibility = View.GONE;
                mDummy.visibility = View.VISIBLE;
            }
        }
    }

    private fun play(channel: Channel) {
        PlayerActivity.ARGS_CHANNEL = channel;
        PlayerActivity.ARGS_CHANNELS = mList;

        if(getPrefs().getSubscriptionStatus(PaywallGoonjFragment.SLUG) == PaymentHelper.Companion.PaymentStatus.STATUS_BILLED){
            // play
            mChannelTitle.text = channel.getName();
            mCurrentChannel = channel;

            // play media
            mPlayerManager.playMedia(MediaModel.getLiveMediaModel(channel, mPrefs.getGlobalBitrate()!!));
            mDummy.visibility = View.GONE

            val paramsArrayList = ArrayList<Params>()
            paramsArrayList.add(Params("id", channel.getId()))
            RestClient(
                getContext(),
                Constants.API_BASE_URL + Constants.Companion.EndPoints.POST_LIVE_VIEWS,
                RestClient.Companion.Method.POST,
                paramsArrayList,
                object : NetworkOperationListener {
                    override fun onSuccess(response: String?) {
                        Logger.println("*** $response")
                    }

                    override fun onFailed(code: Int, reason: String?) {
                        Logger.println("*** $reason")
                    }
                }).exec()

        }else{
            // display paywall
            for(listener in getListeners()){
                listener.goToPaywall();
            }
        }
    }


    override fun onClick(v: View?) {
        when(v){
            mShare -> {
                if(mCurrentChannel != null){
                    Utility.fireShareIntent(getContext(), mCurrentChannel?.getName()!!.toLowerCase().replace(" ", "-"), mCurrentChannel?.getId()!!, true)
                }
            }
        }
    }

    override fun pauseStreaming(){
        mPlayerManager.pause();
    }

    private fun setRecyclerView(recyclerView: RecyclerView) {
        val manager = GridLayoutManager(getContext(), 2)
        recyclerView.layoutManager = manager
    }

    override fun setFullscreen(isFull: Boolean) {
        if(isFull){
            mMainPlayerView.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            mMainPlayerView.layoutParams.height = getHeight(getContext())
        }else{
            mMainPlayerView.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            mMainPlayerView.layoutParams.height = dpToPx(getContext(), 210)
        }

        mPlayerManager.setFullScreen(isFull)
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

    override fun updateNetworkState(isConnected: Boolean) {
        mNetworkStatusTextView.visibility = View.VISIBLE;
        mNetworkStatusTextView.text = if(isConnected) "Back Online" else "No Internet Connection";
        mNetworkStatusTextView.setBackgroundColor(if(isConnected) ContextCompat.getColor(getContext(), R.color.green) else ContextCompat.getColor(getContext(), R.color.colorRed));
        if(isConnected){
            android.os.Handler().postDelayed({
                mNetworkStatusTextView.visibility = View.GONE;
            }, 3000);
        }
        mPlayerManager.updateNetworkState(isConnected);
    }
}