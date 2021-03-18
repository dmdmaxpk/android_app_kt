package com.dmdmax.goonj.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.TabModel
import com.dmdmax.goonj.models.Video
import com.dmdmax.goonj.models.Video.TileType
import com.dmdmax.goonj.screens.fragments.ComedyFragment
import com.dmdmax.goonj.utility.Utility
import com.github.ybq.android.spinkit.SpinKitView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class GenericCategoryAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder> {

    interface OnItemClickListener {
        fun onVideoClick(position: Int, video: Video, tabModel: TabModel);
    }

    private var mListener: OnItemClickListener? = null;
    private lateinit var mContext: Context;
    private lateinit var mListItems: ArrayList<Video>;
    private lateinit var mTabModel: TabModel;

    constructor(
        context: Context,
        listItems: ArrayList<Video>,
        tabModel: TabModel,
        listener: OnItemClickListener?
    ) {
        this.mContext = context;
        this.mListItems = listItems;
        this.mTabModel = tabModel;
        this.mListener = listener;
    }

    internal class ThumbsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val mTitle: TextView
        val mCount: TextView
        val mTimeAgo: TextView
        val mThumbnail: ImageView
        val mSpinKitView: SpinKitView;

        init {
            mTitle = view.findViewById(R.id.title)
            mTimeAgo = view.findViewById(R.id.time_ago)
            mSpinKitView = view.findViewById(R.id.spin_kit)
            mThumbnail = view.findViewById(R.id.thumbnail)
            mCount = view.findViewById(R.id.views_count)
        }
    }

    internal class CarouselViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val mTitle: TextView
        val mCarousel: RecyclerView

        init {
            mTitle = view.findViewById(R.id.channels)
            mCarousel = view.findViewById(R.id.carousel)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (mListItems[position].getTileType()) {
            TileType.TILE_TYPE_THUMBNAIL -> 0
            TileType.TILE_TYPE_RELATED_CHANNELS -> 1
            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> ThumbsViewHolder(
                LayoutInflater.from(mContext).inflate(
                    R.layout.generic_category_item,
                    parent,
                    false
                )
            )

            1 -> CarouselViewHolder(
                LayoutInflater.from(mContext).inflate(
                    R.layout.generic_category_carousel_item,
                    parent,
                    false
                )
            )

            else -> ThumbsViewHolder(
                LayoutInflater.from(mContext).inflate(
                    R.layout.generic_category_item,
                    parent,
                    false
                )
            );
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                val thumbsViewHolder = holder as ThumbsViewHolder
                bindThumbsView(thumbsViewHolder, thumbsViewHolder.adapterPosition, mListener)
            }

            1 -> {
                val viewHolder = holder as CarouselViewHolder
                bindCarouselView(viewHolder, viewHolder.adapterPosition, mListener)
            }

            else -> {
                val thumbsViewHolder = holder as ThumbsViewHolder
                bindThumbsView(thumbsViewHolder, thumbsViewHolder.adapterPosition, mListener)
            }
        }
    }

    private fun bindThumbsView(thumbsViewHolder: ThumbsViewHolder, position: Int, listener: OnItemClickListener?) {
        thumbsViewHolder.mTitle.text = mListItems[position].getTitle();
        thumbsViewHolder.mCount.text = mListItems[position].getViewsCount().toString() + " views";

        thumbsViewHolder.mTimeAgo.text = Utility.getAgoTime(mListItems.get(position).getPublishDtm());

        Picasso.get().load(
            if (mTabModel.getSlug() == ComedyFragment.SLUG) mListItems[position].getThumbnailUrl() else mListItems[position].getThumbnail()).into(thumbsViewHolder.mThumbnail, object : Callback {
            override fun onSuccess() {
                thumbsViewHolder.mSpinKitView.visibility = View.GONE;
            }

            override fun onError(e: Exception) {
                thumbsViewHolder.mThumbnail.setImageResource(R.drawable.no_image_found)
                thumbsViewHolder.mSpinKitView.visibility = View.GONE;
            }
        });

        thumbsViewHolder.mThumbnail.setOnClickListener(View.OnClickListener {
            listener?.onVideoClick(position, mListItems[position], mTabModel)
        })
    }

    private fun bindCarouselView(viewHolder: CarouselViewHolder, position: Int, listener: OnItemClickListener?) {
        setRecyclerView(viewHolder.mCarousel);
        viewHolder.mTitle.text = mTabModel.getCategory() + " CHANNELS";
        viewHolder.mCarousel.adapter = ChannelsCarouselListAdapter(mListItems[position].getChannelsList(), mContext);
    }

    private fun setRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
    }

    override fun getItemCount(): Int {
        return mListItems.size;
    }
}