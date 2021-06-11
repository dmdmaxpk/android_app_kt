package com.dmdmax.goonj.screens.implements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.ProgressBar
import com.dmdmax.goonj.R
import com.dmdmax.goonj.adapters.CategoryWiseVodAdapter
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.models.Video
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.screens.views.VodView
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.JSONParser


class VodImpl: BaseObservableView<VodView.Listener>, VodView {

    private lateinit var mProgressBar: ProgressBar;
    private lateinit var mListView: ListView;

    companion object {
        var PAKISTANI_DRAMAS = "Pakistani Dramas";
        var ENTERTAINMENT_CHANNEL = "Entertainment Channel";
        var SPORTS = "Sports";
        var PROGRAMS = "Programs";

        var SLUG_DRAMA = "dramas";
    }

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.fragment_vod, parent, false));
    }

    override fun initialize() {
        mProgressBar = findViewById(R.id.progress_bar);
        mListView = findViewById(R.id.category_rv);
        populateRecyclerView();
    }

    private fun populateRecyclerView(){
        val list = ArrayList<ArrayList<Video>>();

        // first call
        RestClient(getContext(), Constants.API_BASE_URL + Constants.Companion.EndPoints.SUBCATEGORY + "drama", RestClient.Companion.Method.GET, null, object: NetworkOperationListener{
            override fun onSuccess(response: String?) {
                list.add(JSONParser.getCategory(response, SLUG_DRAMA, PAKISTANI_DRAMAS));

                // second call
                RestClient(getContext(), Constants.API_BASE_URL + Constants.Companion.EndPoints.VIDEO_BY_CATEGORY + "sports", RestClient.Companion.Method.GET, null, object: NetworkOperationListener{
                    override fun onSuccess(response: String?) {
                        list.add(JSONParser.getFeed(response, SPORTS));

                        // third call
                        RestClient(getContext(), Constants.API_BASE_URL + Constants.Companion.EndPoints.VIDEO_BY_CATEGORY + "programs", RestClient.Companion.Method.GET, null, object: NetworkOperationListener{
                            override fun onSuccess(response: String?) {
                                list.add(JSONParser.getFeed(response, PROGRAMS));

                                // set recycler view
                                mListView.adapter = CategoryWiseVodAdapter(list, getContext(), object: CategoryWiseVodAdapter.OnBannerClickListener{
                                    override fun onClick(video: Video, position: Int) {
                                        for (listener in getListeners()) {
                                            listener.onBanner(video);
                                        }
                                    }
                                }, object: CategoryWiseVodAdapter.OnCarouselItemClickListener{
                                    override fun onClick(video: Video, position: Int) {
                                        for (listener in getListeners()) {
                                            listener.onVodClick(video);
                                        }
                                    }
                                });
                                mProgressBar.visibility = View.GONE;

                            }

                            override fun onFailed(code: Int, reason: String?) {
                                TODO("Not yet implemented")
                            }
                        }).exec();

                    }

                    override fun onFailed(code: Int, reason: String?) {
                        TODO("Not yet implemented")
                    }
                }).exec();
            }

            override fun onFailed(code: Int, reason: String?) {
                TODO("Not yet implemented")
            }
        }).exec();
    }
}