package com.dmdmax.goonj.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.models.Anchor
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.screens.activities.PlayerActivity
import com.dmdmax.goonj.utility.Logger
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChannelsCarouselListAdapter: RecyclerView.Adapter<ChannelsCarouselListAdapter.MyViewHolder> {

    private var list: ArrayList<Channel>? = null
    private var context: Context? = null

    constructor(
        list: ArrayList<Channel>?,
        context: Context?
    ) {
        this.list = list
        this.context = context;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.channel_adapter_item, null)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Logger.println(list!![holder.adapterPosition].getThumbnail());
        Picasso.get().load(list!![holder.adapterPosition].getThumbnail())
            .into(holder.thumbnail, object : Callback {
                override fun onSuccess() {

                }

                override fun onError(e: Exception) {
                    holder.thumbnail.setImageResource(R.drawable.no_image_found)
                }
            }
            )
        holder.thumbnail.setOnClickListener {
            if(!(context as BaseActivity is PlayerActivity)){
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra(PlayerActivity.ARGS_NAME, list!![holder.adapterPosition].getName())
                intent.putExtra(PlayerActivity.ARGS_ID, list!![holder.adapterPosition].getId())
                intent.putExtra(PlayerActivity.ARGS_HLS, list!![holder.adapterPosition].getHlsLink())
                intent.putExtra(PlayerActivity.ARGS_THUMBNAIL, list!![holder.adapterPosition].getThumbnail())
                intent.putExtra(PlayerActivity.ARGS_CHANNELS, list);
                context!!.startActivity(intent)
            }else{
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra(PlayerActivity.ARGS_NAME, list!![holder.adapterPosition].getName())
                intent.putExtra(PlayerActivity.ARGS_ID, list!![holder.adapterPosition].getId())
                intent.putExtra(PlayerActivity.ARGS_HLS, list!![holder.adapterPosition].getHlsLink())
                intent.putExtra(PlayerActivity.ARGS_THUMBNAIL, list!![holder.adapterPosition].getThumbnail())
                intent.putExtra(PlayerActivity.ARGS_CHANNELS, list);
                context!!.startActivity(intent)
                (context as BaseActivity).finish()
            }
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var thumbnail: CircleImageView

        init {
            thumbnail = view.findViewById(R.id.thumb)
        }
    }
}