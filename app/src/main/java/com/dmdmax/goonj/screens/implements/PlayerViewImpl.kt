package com.dmdmax.goonj.screens.implements

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.adapters.ChannelsCarouselListAdapter
import com.dmdmax.goonj.adapters.GenericCategoryAdapter
import com.dmdmax.goonj.adapters.HeadlinesCarouselListAdapter
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.models.*
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.player.ExoPlayerManager
import com.dmdmax.goonj.screens.activities.PlayerActivity
import com.dmdmax.goonj.screens.fragments.paywall.PaywallBinjeeFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.views.PlayerView
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.*
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.set


class PlayerViewImpl: BaseObservableView<PlayerView.Listener>, PlayerView, View.OnClickListener {

    private lateinit var mMetaLayout: LinearLayout;
    private lateinit var mHeaderLayout: FrameLayout;

    private lateinit var mTitle: TextView;
    private lateinit var mChannelTitle: TextView;
    private lateinit var mLiveOrVod: TextView;
    private lateinit var mBack: ImageView;
    private lateinit var mPlayerManager: ExoPlayerManager;
    private lateinit var mPlayer: com.google.android.exoplayer2.ui.PlayerView;
    private lateinit var mPrefs: GoonjPrefs;

    private lateinit var mShare: LinearLayout;
    private lateinit var mLiveChannelRecommendationLayout: LinearLayout;
    private lateinit var mRecommendedVideosLayout: LinearLayout;
    private lateinit var mEpisodesLayout: LinearLayout;

    private lateinit var mRecommendedLiveChannels: RecyclerView;
    private lateinit var mEpisodes: RecyclerView;
    private lateinit var mRecommendedHeadlines: RecyclerView;
    private lateinit var mRecommendedEntertainmentChannels: RecyclerView;
    private lateinit var mRecommendedVideos: RecyclerView;

    private lateinit var mNetworkStatusTextView: TextView;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.activity_player, parent, false));
    }

    override fun initialize(model: MediaModel, list: ArrayList<Channel>?) {

        mMetaLayout = findViewById(R.id.meta_layout);
        mHeaderLayout = findViewById(R.id.header);
        mPlayer = findViewById(R.id.video_view);

        mTitle = findViewById(R.id.title);
        mRecommendedVideosLayout = findViewById(R.id.recommended_videos_layout);
        mNetworkStatusTextView = findViewById(R.id.network_status);
        mChannelTitle = findViewById(R.id.channel_title);
        mLiveOrVod = findViewById(R.id.liveOrVod);
        mShare = findViewById(R.id.share);
        mBack = findViewById(R.id.back_arrow);
        mRecommendedLiveChannels = findViewById(R.id.recommended_live_channels);
        mRecommendedVideos = findViewById(R.id.recommended_videos);
        mLiveChannelRecommendationLayout = findViewById(R.id.live_channels_recommendation_layout);
        mEpisodesLayout = findViewById(R.id.episodes_layout);

        mRecommendedHeadlines = findViewById(R.id.recommended_headlines);
        mRecommendedEntertainmentChannels = findViewById(R.id.recommended_entertainment_channels);
        mEpisodes = findViewById(R.id.episodes);

        setRecyclerView(mRecommendedHeadlines);
        setRecyclerView(mRecommendedLiveChannels)
        setRecyclerView(mRecommendedEntertainmentChannels)
        setVerticalRecyclerView(mRecommendedVideos)
        setVerticalRecyclerView(mEpisodes)

        mBack.setOnClickListener(this);
        mShare.setOnClickListener(this);

        mPlayerManager = ExoPlayerManager();
        mPrefs = GoonjPrefs(getContext());

        // init player
        mPlayerManager.init(getContext(), mPlayer);

        displayView(model, list);
    }

    private fun displayView(model: MediaModel, list: ArrayList<Channel>?){
        if(model.title?.length!! > 35){
            val scale: Float = getContext().resources.displayMetrics.density
            val dpAsPixels: Float = (50 * scale + 0.5f)
            mTitle.setPadding((dpAsPixels).toInt(), 0, 0, 0);
        }else{
            val scale: Float = getContext().resources.displayMetrics.density
            val dpAsPixels: Float = (0 * scale + 0.5f)
            mTitle.setPadding((dpAsPixels).toInt(), 0, 0, 0);
        }

        mTitle.text = model.title;
        mChannelTitle.text = model.title;

        mLiveOrVod.text = if (model.isLive) "LIVE" else "VOD";

        EventManager.getInstance(getContext()).fireEvent(if(model.isLive) EventManager.Events.PLAY_LIVE else EventManager.Events.PLAY_VOD);

        val event = "${model.category!!.capitalize()}_${EventManager.Events.PLAY_CONTENT}${model.title!!.replace(" ", "_")}";
        Logger.println("EVENT: "+event);
        EventManager.getInstance(getContext()).fireEvent(event);

        if (model.isLive) {
            mEpisodesLayout.visibility = View.GONE;
            mLiveChannelRecommendationLayout.visibility = View.VISIBLE
            mPlayerManager.playMedia(model);

            val list = getPrefs().getChannels() as ArrayList<Channel>?;
            mRecommendedLiveChannels.adapter = ChannelsCarouselListAdapter(list, getContext(), object: ChannelsCarouselListAdapter.OnItemClickListener {
                override fun onClick(channel: Channel, position: Int) {
                    for(listener in getListeners()){
                        listener.onLiveChannelClick(channel);
                    }
                }
            });

            displayHeadlines();
            displayEntertainmentChannels();
            mRecommendedVideosLayout.visibility = View.GONE;
        } else {
            if(
                model.category == PaywallComedyFragment.SLUG ||
                model.category == PaywallBinjeeFragment.SLUG ||
                model.category == VodImpl.SLUG_DRAMA){
                displayEpisodes();

                mEpisodesLayout.visibility = View.VISIBLE;
                mLiveChannelRecommendationLayout.visibility = View.GONE
            }else{
                mPlayerManager.playMedia(model);

                mEpisodesLayout.visibility = View.GONE;
                mLiveChannelRecommendationLayout.visibility = View.VISIBLE

                // Set bottom views
                if(list != null){
                    mRecommendedLiveChannels.adapter = ChannelsCarouselListAdapter(list, getContext(), object: ChannelsCarouselListAdapter.OnItemClickListener {
                        override fun onClick(channel: Channel, position: Int) {
                            for(listener in getListeners()){
                                listener.onLiveChannelClick(channel);
                            }
                        }
                    });
                } else {
                    val list = getPrefs().getChannels() as ArrayList<Channel>?;
                    var liveChannels: ArrayList<Channel> = list!!.filter { s -> s.getCategory() == "news" } as ArrayList<Channel>
                    mRecommendedLiveChannels.adapter = ChannelsCarouselListAdapter(liveChannels, getContext(), object: ChannelsCarouselListAdapter.OnItemClickListener {
                        override fun onClick(channel: Channel, position: Int) {
                            for(listener in getListeners()){
                                listener.onLiveChannelClick(channel);
                            }
                        }
                    });
                }

                displayHeadlines();
                displayEntertainmentChannels();
                displayRecommendedVideos();
            }
        }

        val paramsArrayList = ArrayList<Params>()
        paramsArrayList.add(Params("id", model.id))
        RestClient(
            getContext(),
            Constants.API_BASE_URL + (if(model.isLive) Constants.Companion.EndPoints.POST_LIVE_VIEWS else Constants.Companion.EndPoints.POST_VIDEO_VIEWS),
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


        if(!model.isLive){
            /*MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf("2138f018-f7c0-46b1-bc25-ca9770d3c4f5"))
                    .build()
            )*/
            MobileAds.initialize(getContext());
            val manager = GoonjAdManager();
            manager.loadInterstitialAd(getContext());
        }
    }

    private fun displayEpisodes(){
        if(PlayerActivity.ARGS_VIDEO!!.getSlug() == PaywallComedyFragment.SLUG){
            // ideation video

            val postBody: MutableMap<String, String> = HashMap()
            postBody["videos_id"] = PlayerActivity.ARGS_VIDEO?.getId()!!;

            RestClient(getContext(), Constants.COMEDY_BASE_URL + Constants.Companion.EndPoints.COMEDY_GET_EPISODES, RestClient.Companion.Method.POST, null, object : NetworkOperationListener {
                override fun onSuccess(response: String?) {
                    val mVideos: ArrayList<Video> = arrayListOf()
                    val mObj = JSONArray(response)

                    for (i in 0 until mObj.length()) {
                        mVideos.add(Episode.getVideo(mObj.getJSONObject(i), PaywallComedyFragment.SLUG));
                    }


                    mEpisodes.adapter = GenericCategoryAdapter(getContext(), mVideos, TabModel.getEpisodeTab(), object: GenericCategoryAdapter.OnItemClickListener{
                        override fun onVideoClick(position: Int, video: Video, tabModel: TabModel?) {
                            PlayerActivity.ARGS_CHANNEL = null;
                            PlayerActivity.ARGS_VIDEO = null;
                            PlayerActivity.ARGS_VIDEO = video;
                            displayView(MediaModel.getVodMediaModel(video, mPrefs.getGlobalBitrate()!!), null);
                        }
                    })

                    mPlayerManager.playMedia(MediaModel.getVodMediaModel(mVideos[0], mPrefs.getGlobalBitrate()!!))
                    mChannelTitle.text = mVideos[0].getTitle();
                }

                override fun onFailed(code: Int, reason: String?) {}
            }).exec(PaywallComedyFragment.SLUG, postBody);
        }
        else if(PlayerActivity.ARGS_VIDEO!!.getSlug() == PaywallBinjeeFragment.SLUG) {
            // Binjee videos

            val postBody: java.util.ArrayList<Params> = arrayListOf(
                Params("channel", "APP"),
                Params("refId", "20170101112222"),
                Params("subcat_id", PlayerActivity.ARGS_VIDEO!!.getId()!!)
            );

            RestClient(getContext(), Constants.BINJEE_CONTENT_API_BASE_URL + Constants.Companion.EndPoints.GET_BINJEE_VIDEOS, RestClient.Companion.Method.POST, postBody, object : NetworkOperationListener {
                override fun onSuccess(response: String?) {
                    val mVideos: ArrayList<Video> = arrayListOf()
                    val mObj = JSONObject(response).getJSONArray("info");

                    for (i in 0 until mObj.length()) {
                        mVideos.add(Episode.getVideo(mObj.getJSONObject(i), PaywallBinjeeFragment.SLUG));
                    }


                    mEpisodes.adapter = GenericCategoryAdapter(getContext(), mVideos, TabModel.getEpisodeTab(), object: GenericCategoryAdapter.OnItemClickListener{
                        override fun onVideoClick(position: Int, video: Video, tabModel: TabModel?) {
                            PlayerActivity.ARGS_CHANNEL = null;
                            PlayerActivity.ARGS_VIDEO = null;
                            PlayerActivity.ARGS_VIDEO = video;
                            displayView(MediaModel.getVodMediaModel(video, mPrefs.getGlobalBitrate()!!), null);
                        }
                    })

                    mPlayerManager.playMedia(MediaModel.getVodMediaModel(mVideos[0], mPrefs.getGlobalBitrate()!!))
                    mChannelTitle.text = mVideos[0].getTitle();
                }

                override fun onFailed(code: Int, reason: String?) {}
            }).exec(PaywallBinjeeFragment.SLUG, null);
        }else{
            // dmd video
            if(PlayerActivity.ARGS_VIDEO!!.getSlug() == VodImpl.SLUG_DRAMA || PlayerActivity.ARGS_VIDEO!!.getCategory() == VodImpl.SLUG_DRAMA){
                mChannelTitle.text = "";

                // dramas, not simple vods
                RestClient(getContext(), Constants.API_BASE_URL + Constants.Companion.EndPoints.VIDEO_BY_SUB_CATEGORY + PlayerActivity.ARGS_VIDEO!!.getTitle() + "&limit=100&skip=0", RestClient.Companion.Method.GET, null, object : NetworkOperationListener {
                    override fun onSuccess(response: String?) {
                        val mVideos: ArrayList<Video> = JSONParser.getFeed(response, PlayerActivity.ARGS_VIDEO!!.getSlug())

                        mChannelTitle.text = mVideos[0].getTitle();
                        mPlayerManager.playMedia(MediaModel.getVodMediaModel(mVideos[0], mPrefs.getGlobalBitrate()!!));
                        mEpisodes.adapter = GenericCategoryAdapter(getContext(), mVideos, null, object: GenericCategoryAdapter.OnItemClickListener{
                            override fun onVideoClick(position: Int, video: Video, tabModel: TabModel?) {
                                PlayerActivity.ARGS_CHANNEL = null;
                                PlayerActivity.ARGS_VIDEO = null;
                                PlayerActivity.ARGS_VIDEO = video;
                                displayView(MediaModel.getVodMediaModel(video, mPrefs.getGlobalBitrate()!!), null);
                            }
                        })
                    }

                    override fun onFailed(code: Int, reason: String?) {
                        Logger.println("API Failed: $reason")
                    }
                }).exec();

            }else{
                // simple vods

                mPlayerManager.playMedia(MediaModel.getVodMediaModel(PlayerActivity.ARGS_VIDEO!!, mPrefs.getGlobalBitrate()!!));
                RestClient(getContext(), Constants.API_BASE_URL + Constants.Companion.EndPoints.VIDEO_BY_CATEGORY + PlayerActivity.ARGS_VIDEO!!.getCategory(), RestClient.Companion.Method.GET, null, object : NetworkOperationListener {
                    override fun onSuccess(response: String?) {
                        val mVideos: ArrayList<Video> = JSONParser.getFeed(response, PlayerActivity.ARGS_VIDEO!!.getSlug())
                        mEpisodes.adapter = GenericCategoryAdapter(getContext(), mVideos, null, object: GenericCategoryAdapter.OnItemClickListener{
                            override fun onVideoClick(position: Int, video: Video, tabModel: TabModel?) {
                                PlayerActivity.ARGS_CHANNEL = null;
                                PlayerActivity.ARGS_VIDEO = null;
                                PlayerActivity.ARGS_VIDEO = video;
                                displayView(MediaModel.getVodMediaModel(video, mPrefs.getGlobalBitrate()!!), null);
                            }
                        })
                    }

                    override fun onFailed(code: Int, reason: String?) {}
                }).exec();
            }

        }
    }

    private fun displayRecommendedVideos() {
        mRecommendedVideosLayout.visibility = View.GONE;
        var queryString = "?id=${PlayerActivity.ARGS_VIDEO!!.getId()}";
        if(mPrefs.getMsisdn(PaywallGoonjFragment.SLUG) != null && !mPrefs.getUserId(PaywallGoonjFragment.SLUG).equals("null")){
            queryString = queryString.plus("&msisdn=${mPrefs.getMsisdn(PaywallGoonjFragment.SLUG)}");
        }

        RestClient(getContext(), Constants.API_BASE_URL + Constants.Companion.EndPoints.RECOMMENDED_VIDEOS + queryString, RestClient.Companion.Method.GET, null, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                mRecommendedVideosLayout.visibility = View.VISIBLE;
                mRecommendedVideos.adapter = GenericCategoryAdapter(getContext(), JSONParser.getFeedFlipped(response, null), null, object: GenericCategoryAdapter.OnItemClickListener {
                    override fun onVideoClick(position: Int, video: Video, tabModel: TabModel?) {
                        PlayerActivity.ARGS_CHANNEL = null;
                        PlayerActivity.ARGS_VIDEO = null;
                        PlayerActivity.ARGS_VIDEO = video;
                        displayView(MediaModel.getVodMediaModel(video, mPrefs.getGlobalBitrate()!!), null);
                    }
                });
            }

            override fun onFailed(code: Int, reason: String?) {
                getLogger().println("onFailed:- " + reason);
                mRecommendedVideosLayout.visibility = View.GONE;
            }
        }).exec();
    }

    private fun displayHeadlines() {
        RestClient(getContext(), Constants.API_BASE_URL + Constants.Companion.EndPoints.VIDEO_BY_CATEGORY + "news", RestClient.Companion.Method.GET, null, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                mRecommendedHeadlines.adapter = HeadlinesCarouselListAdapter(JSONParser.getFeed(response, "headlines"), getContext(), object: HeadlinesCarouselListAdapter.OnItemClickListener {
                    override fun onClick(video: Video) {
                        PlayerActivity.ARGS_CHANNEL = null;
                        PlayerActivity.ARGS_VIDEO = null;
                        PlayerActivity.ARGS_VIDEO = video;
                        displayView(MediaModel.getVodMediaModel(video, mPrefs.getGlobalBitrate()!!), null);
                    }
                });
            }

            override fun onFailed(code: Int, reason: String?) {
                getLogger().println("onFailed:- " + reason);
            }
        }).exec();
    }

    private fun displayEntertainmentChannels() {
        val list = getPrefs().getChannels() as ArrayList<Channel>?;
        var entertainmentChannels: ArrayList<Channel> = list!!.filter { s -> s.getCategory() == "entertainment" } as ArrayList<Channel>
        mRecommendedEntertainmentChannels.adapter = ChannelsCarouselListAdapter(entertainmentChannels, getContext(), object: ChannelsCarouselListAdapter.OnItemClickListener {
            override fun onClick(channel: Channel, position: Int) {
                for(listener in getListeners()){
                    listener.onLiveChannelClick(channel);
                }
            }
        });
    }

    override fun onClick(v: View?) {
        when(v){
            mBack -> {
                mPlayer.player.playWhenReady = false;
                for (listener in getListeners()) {
                    listener.goBack();
                }
            }

            mShare -> {
                if(PlayerActivity.ARGS_VIDEO != null){
                    Utility.fireShareIntent(getContext(), PlayerActivity.ARGS_VIDEO!!.getTitle()!!, PlayerActivity.ARGS_VIDEO!!.getId()!!, false);
                }else{
                    Utility.fireShareIntent(getContext(), PlayerActivity.ARGS_CHANNEL!!.getName(), PlayerActivity.ARGS_CHANNEL!!.getId(), true);
                }

            }
        }
    }

    override fun pauseStreaming(){
        mPlayerManager.pause();
    }

    override fun startStreaming(){
        mPlayerManager.resume();
    }

    override fun releasePlayer() {
        mPlayerManager.release();
    }

    override fun getPlayer(): com.google.android.exoplayer2.ui.PlayerView {
        return mPlayer;
    }

    override fun setFullscreen(isFull: Boolean) {
        if(isFull){
            mPlayer.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            mPlayer.layoutParams.height = getHeight(getContext())

            mHeaderLayout.visibility = View.GONE;
            mMetaLayout.visibility = View.GONE;
        }else{
            mPlayer.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            mPlayer.layoutParams.height = dpToPx(getContext(), 210)

            mHeaderLayout.visibility = View.VISIBLE;
            mMetaLayout.visibility = View.VISIBLE;
        }

        mPlayerManager.setFullScreen(isFull)
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

    private fun setRecyclerView(recyclerView: RecyclerView) {
        val horizontalLayoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = horizontalLayoutManager
    }

    private fun setVerticalRecyclerView(recyclerView: RecyclerView) {
        recyclerView.isNestedScrollingEnabled = false;
        val horizontalLayoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = horizontalLayoutManager
    }
}