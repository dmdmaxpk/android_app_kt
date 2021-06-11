package com.dmdmax.goonj.screens.implements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.adapters.ChannelsCarouselListAdapter
import com.dmdmax.goonj.adapters.GenericCategoryAdapter
import com.dmdmax.goonj.adapters.HeadlinesCarouselListAdapter
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.models.*
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.player.ExoPlayerManager
import com.dmdmax.goonj.screens.activities.PlayerActivity
import com.dmdmax.goonj.screens.fragments.paywall.PaywallBinjeeFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.views.PlayerView
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.JSONParser
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Utility
import com.google.android.exoplayer2.Player
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.MutableMap
import kotlin.collections.filter
import kotlin.collections.set


class PlayerViewImpl: BaseObservableView<PlayerView.Listener>, PlayerView, View.OnClickListener {

    private lateinit var mTitle: TextView;
    private lateinit var mChannelTitle: TextView;
    private lateinit var mLiveOrVod: TextView;
    private lateinit var mBack: ImageView;
    private lateinit var mPlayerManager: ExoPlayerManager;
    private lateinit var mPlayer: com.google.android.exoplayer2.ui.PlayerView;
    private lateinit var mPrefs: GoonjPrefs;

    private lateinit var mShare: LinearLayout;
    private lateinit var mLiveChannelRecommendationLayout: LinearLayout;
    private lateinit var mEpisodesLayout: LinearLayout;

    private lateinit var mRecommendedLiveChannels: RecyclerView;
    private lateinit var mEpisodes: RecyclerView;
    private lateinit var mRecommendedHeadlines: RecyclerView;
    private lateinit var mRecommendedEntertainmentChannels: RecyclerView;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.activity_player, parent, false));
    }

    override fun initialize(model: MediaModel, list: ArrayList<Channel>?) {
        mTitle = findViewById(R.id.title);
        mChannelTitle = findViewById(R.id.channel_title);
        mLiveOrVod = findViewById(R.id.liveOrVod);
        mShare = findViewById(R.id.share);
        mBack = findViewById(R.id.back_arrow);
        mRecommendedLiveChannels = findViewById(R.id.recommended_live_channels);
        mLiveChannelRecommendationLayout = findViewById(R.id.live_channels_recommendation_layout);
        mEpisodesLayout = findViewById(R.id.episodes_layout);

        mRecommendedHeadlines = findViewById(R.id.recommended_headlines);
        mRecommendedEntertainmentChannels = findViewById(R.id.recommended_entertainment_channels);
        mEpisodes = findViewById(R.id.episodes);

        setRecyclerView(mRecommendedHeadlines);
        setRecyclerView(mRecommendedLiveChannels)
        setRecyclerView(mRecommendedEntertainmentChannels)
        setVerticalRecyclerView(mEpisodes)

        mBack.setOnClickListener(this);
        mShare.setOnClickListener(this);

        mPlayer = findViewById(R.id.video_view);

        mPlayerManager = ExoPlayerManager();
        mPrefs = GoonjPrefs(getContext());

        // init player
        mPlayerManager.init(getContext(), mPlayer);

        displayView(model, list);
    }

    private fun displayView(model: MediaModel, list: ArrayList<Channel>?){
        mTitle.text = model.title;
        mChannelTitle.text = model.title;

        mLiveOrVod.text = if (model.isLive) "LIVE" else "VOD";

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
                }else{
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
            }
        }
    }

    private fun displayEpisodes(){
        if(PlayerActivity.ARGS_VIDEO!!.getThumbnailUrl() != null){
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
        else{
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

    private fun displayHeadlines() {
        RestClient(getContext(), Constants.API_BASE_URL + Constants.Companion.EndPoints.VIDEO_BY_CATEGORY + "news", RestClient.Companion.Method.GET, null, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                mRecommendedHeadlines.adapter = HeadlinesCarouselListAdapter(JSONParser.getFeed(response, null), getContext(), object: HeadlinesCarouselListAdapter.OnItemClickListener {
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

    private fun setRecyclerView(recyclerView: RecyclerView) {
        val horizontalLayoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = horizontalLayoutManager
    }

    private fun setVerticalRecyclerView(recyclerView: RecyclerView) {
        val horizontalLayoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = horizontalLayoutManager
    }
}