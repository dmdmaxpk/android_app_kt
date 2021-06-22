package com.dmdmax.goonj.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.Video
import com.dmdmax.goonj.screens.implements.VodImpl
import com.dmdmax.goonj.utility.Logger
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class CategoryWiseVodAdapter: BaseAdapter {

    private var list: ArrayList<ArrayList<Video>>;
    private var context: Context? = null
    private var listener: OnBannerClickListener? = null;
    private var listener2: OnCarouselItemClickListener? = null;

    constructor(list: ArrayList<ArrayList<Video>>, context: Context?) {
        this.list = list
        this.context = context;
    }

    interface OnBannerClickListener{
        fun onClick(video: Video, position: Int)
    }

    interface OnCarouselItemClickListener{
        fun onClick(video: Video, position: Int)
    }

    constructor(list: ArrayList<ArrayList<Video>>, context: Context?, listener: OnBannerClickListener?, listener2: OnCarouselItemClickListener?) {
        this.list = list
        this.context = context;
        this.listener = listener;
        this.listener2 = listener2;
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): java.util.ArrayList<Video>? {
        return list[position];
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.vod_fragment_list_item, parent, false);
        var thumbnail: ImageView = view.findViewById(R.id.thumbnail);
        var title: TextView = view.findViewById(R.id.title);
        var others: RecyclerView = view.findViewById(R.id.others);

        val key: String? = if(list[position][0].getKey() != null) list[position][0].getKey() else list[position][0].getSlug();

        title.text = key;
        Picasso.get().load(if(position == 0) list[position][0].getPosterUrl() else list[position][0].getThumbnail(null))
            .into(thumbnail, object : Callback {
                override fun onSuccess() {

                }

                override fun onError(e: Exception) {
                    thumbnail.setImageResource(R.drawable.no_image_found)
                }
            })

        thumbnail.setOnClickListener {
            if(listener != null){
                listener!!.onClick(list[position][0], position);
            }
        }
        others.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);

        if(position == 0){
            // dramas
            var tempList = arrayListOf<Video>()
            tempList.addAll(list[position]);
            tempList.removeAt(0);
            others.adapter = ComedyBannerCarouselListAdapter(tempList, context, object : ComedyBannerCarouselListAdapter.OnItemClickListener {
                override fun onClick(video: Video, position: Int) {
                    if(listener2 != null){
                        listener2!!.onClick(video, position);
                    }
                }
            });
        }else{
            //vods
            var tempList = arrayListOf<Video>()
            tempList.addAll(list[position]);
            tempList.removeAt(0);

            others.adapter = VodCarouselListAdapter(tempList, context, object : VodCarouselListAdapter.OnItemClickListener {
                override fun onClick(video: Video, position: Int) {
                    if(listener2 != null){
                        listener2!!.onClick(video, position);
                    }
                }
            });
        }

        return view;
    }
}