package com.dmdmax.goonj.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.base.BaseViewFactory
import com.dmdmax.goonj.models.Anchor
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.screens.activities.PlayerActivity
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Logger
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChannelsCarouselListAdapter: RecyclerView.Adapter<ChannelsCarouselListAdapter.MyViewHolder> {

    private var list: ArrayList<Channel>? = null
    private var context: Context? = null
    private var listener: OnItemClickListener? = null;

    constructor(list: ArrayList<Channel>?, context: Context?) {
        this.list = list
        this.context = context;
    }

    interface OnItemClickListener{
        fun onClick(channel: Channel, position: Int)
    }

    constructor(list: ArrayList<Channel>?, context: Context?, listener: OnItemClickListener?) {
        this.list = list
        this.context = context;
        this.listener = listener;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.bottom_live_tv_adapter_item, null)
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
            if(listener != null){
                listener?.onClick(list!![holder.adapterPosition], holder.adapterPosition);
            }else{
                val mPrefs = GoonjPrefs(context);
                PlayerActivity.ARGS_VIDEO = null;
                PlayerActivity.ARGS_CHANNEL = list!![holder.adapterPosition];
                PlayerActivity.ARGS_CHANNELS = list!!;

                if(mPrefs.getSubscriptionStatus(PaywallGoonjFragment.SLUG) == PaymentHelper.Companion.PaymentStatus.STATUS_BILLED){
                    if(!(context as BaseActivity is PlayerActivity)){
                        BaseViewFactory(LayoutInflater.from(context)).toPlayerScreen(list!![holder.adapterPosition], list!!);
                    }else{
                        BaseViewFactory(LayoutInflater.from(context)).toPlayerScreen(list!![holder.adapterPosition], list!!);
                        (context as BaseActivity).finish()
                    }

                }else{
                    BaseViewFactory(LayoutInflater.from(context)).toPaywallScreen(list!![holder.adapterPosition], PaywallGoonjFragment.SLUG);
                }
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