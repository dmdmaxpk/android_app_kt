package com.dmdmax.goonj.screens.implements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.adapters.GenericCategoryAdapter
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.models.TabModel
import com.dmdmax.goonj.models.Video
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.views.GenericCategoryView
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.JSONParser
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Utility

class GenericCategoryViewImpl: BaseObservableView<GenericCategoryView.Listener>, GenericCategoryView {

    private lateinit var mVideosRecyclerView: RecyclerView;
    private lateinit var mProgressBar: ProgressBar;

    private lateinit var mGenericAdapter: GenericCategoryAdapter;

    private var mList: ArrayList<Video> = arrayListOf();

    private var mLoadMoreInProgress: Boolean = false;
    private lateinit var tabModel: TabModel;
    private lateinit var mFooter: Video;

    constructor(inflater: LayoutInflater, parent: ViewGroup) {
        setRootView(inflater.inflate(R.layout.fragment_generic_category, parent, false));
    }

    override fun initialize() {
        mVideosRecyclerView = findViewById(R.id.videos_recycler_view);
        mProgressBar = findViewById(R.id.progress_bar);
        setRecyclerView(mVideosRecyclerView, null);
    }

    override fun loadVideos(category: TabModel){
        tabModel = category;
        RestClient(
            getContext(),
            Constants.API_BASE_URL + Constants.Companion.EndPoints.VIDEO_BY_CATEGORY + category.getCategory(),
            RestClient.Companion.Method.GET,
            null,
            object : NetworkOperationListener {
                override fun onSuccess(response: String?) {
                    mList.addAll(JSONParser.getFeed(response, null));
                    mFooter = Video(Video.TileType.TILE_TYPE_FOOTER);
                    mList.add(mFooter);
                    var relatedChannels = Utility.getCategoryWiseChannel(null, category.getCategory()!!, getContext())

                    if(relatedChannels.size > 0){
                        val carousel = Video(Video.TileType.TILE_TYPE_RELATED_CHANNELS);
                        carousel.setChannelsList(relatedChannels);
                        mList.add(0, carousel);
                    }

                    mGenericAdapter = GenericCategoryAdapter(
                        getContext(),
                        mList,
                        category,
                        object : GenericCategoryAdapter.OnItemClickListener {
                            override fun onVideoClick(
                                position: Int,
                                video: Video,
                                tabModel: TabModel?
                            ) {
                                for(listener in getListeners()){
                                    listener.onItemClick(video);
                                }
                            }
                        });

                    mVideosRecyclerView.adapter = mGenericAdapter;
                    mProgressBar.visibility = View.GONE;
                }

                override fun onFailed(code: Int, reason: String?) {
                    getToaster().printToast(getContext(), "Failed to fetch data from APIs")
                }
            }).exec();
    }

    private fun setRecyclerView(recyclerView: RecyclerView, style: String?) {
        if (style != null && style == "grid") {
            recyclerView.layoutManager = GridLayoutManager(getContext(), 2)
        } else {
            recyclerView.layoutManager = LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false)
            recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if((recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == mList.size - 1){
                        if(!mLoadMoreInProgress){
                            mLoadMoreInProgress = true;

                            Logger.println("ITS A BOTTOM")

                            RestClient(
                                getContext(),
                                Constants.API_BASE_URL + Constants.Companion.EndPoints.VIDEO_BY_CATEGORY + tabModel.getCategory() + "&limit=15&skip=" + (mList.size - 1),
                                RestClient.Companion.Method.GET,
                                null,
                                object : NetworkOperationListener {
                                    override fun onSuccess(response: String?) {
                                        mList.remove(mFooter);
                                        mList.addAll(JSONParser.getFeed(response, null));
                                        mList.add(mFooter)
                                        mGenericAdapter.notifyDataSetChanged();
                                        mLoadMoreInProgress = false;
                                    }

                                    override fun onFailed(code: Int, reason: String?) {
                                        getToaster().printToast(getContext(), "Failed to fetch data from APIs")
                                    }
                                }).exec();
                        }
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
        }
    }
}