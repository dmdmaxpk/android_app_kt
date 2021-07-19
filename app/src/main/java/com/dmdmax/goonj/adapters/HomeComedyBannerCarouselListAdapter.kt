package com.dmdmax.goonj.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.models.Video
import com.dmdmax.goonj.utility.Logger
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class HomeComedyBannerCarouselListAdapter: RecyclerView.Adapter<HomeComedyBannerCarouselListAdapter.MyViewHolder> {

    private var list: ArrayList<Video>? = null
    private var context: Context? = null
    private var listener: OnItemClickListener? = null;

    constructor(list: ArrayList<Video>?, context: Context?) {
        this.list = list
        this.context = context;
    }

    interface OnItemClickListener{
        fun onClick(video: Video, position: Int)
    }

    constructor(list: ArrayList<Video>?, context: Context?, listener: OnItemClickListener?) {
        this.list = list
        this.context = context;
        this.listener = listener;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.home_comedy_banner_adapter_item, null)
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = list!![holder.adapterPosition].getTitle();
        Logger.println("POSTER ${list!![holder.adapterPosition].getTitle()} - "+list!![holder.adapterPosition].getPosterUrl())
        Picasso.get().load(list!![holder.adapterPosition].getPosterUrl())
            .into(holder.thumbnail, object : Callback {
                override fun onSuccess() {

                }

                override fun onError(e: Exception) {
                    holder.thumbnail.setImageResource(R.drawable.no_image_found)
                }
            }
            )

        holder.thumbnail.setOnClickListener {
            if(listener != null){
                listener?.onClick(list!![holder.adapterPosition], holder.adapterPosition);
            }
        }

        holder.layout.clipToOutline = true;
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var thumbnail: ImageView
        var name: TextView
        var layout: LinearLayout;

        init {
            thumbnail = view.findViewById(R.id.thumbnail)
            name = view.findViewById(R.id.name)
            layout = view.findViewById(R.id.layout)
        }
    }
}