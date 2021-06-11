package com.dmdmax.goonj.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.models.Anchor
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.Video
import com.dmdmax.goonj.screens.activities.PlayerActivity
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Toaster
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class HeadlinesCarouselListAdapter: RecyclerView.Adapter<HeadlinesCarouselListAdapter.MyViewHolder> {

    private var list: ArrayList<Video>? = null
    private var context: Context? = null
    private var listener: OnItemClickListener? = null;

    constructor(
        list: ArrayList<Video>?,
        context: Context?,
        listener: OnItemClickListener?
    ) {
        this.list = list
        this.context = context;
        this.listener = listener;
    }

    interface OnItemClickListener{
        fun onClick(video: Video)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.headline_adapter_item, null)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Picasso.get().load(list!![holder.adapterPosition].getThumbnail(null))
            .into(holder.thumbnail, object : Callback {
                override fun onSuccess() {

                }

                override fun onError(e: Exception) {
                    holder.thumbnail.setImageResource(R.drawable.no_image_found)
                }
        })

        holder.thumbnail.setOnClickListener {
            if(listener != null){
                listener?.onClick(list!![holder.adapterPosition])
            }
        }

        holder.title.text = list!![holder.adapterPosition].getTitle()!!;
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var thumbnail: ImageView;
        var title: TextView;

        init {
            thumbnail = view.findViewById(R.id.thumb);
            title = view.findViewById(R.id.title);
        }
    }
}