package com.dmdmax.goonj.adapters

import android.app.ActionBar
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import com.dmdmax.goonj.R
import com.dmdmax.goonj.models.SliderModel
import com.squareup.picasso.Picasso

class HomeSliderAdapter: PagerAdapter {

    private var mList: ArrayList<SliderModel>;
    private var mContext: Context;
    private var listener: OnItemClickListener;

    interface OnItemClickListener{
        fun onClick(mode: SliderModel, position: Int)
    }

    constructor(mContext: Context, mList: ArrayList<SliderModel>, listener: OnItemClickListener) {
        this.mContext = mContext;
        this.mList = mList;
        this.listener = listener;
    }

    override fun getCount(): Int {
        return this.mList.size;
    }

    fun getDataSet(): ArrayList<SliderModel> {
        return mList;
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return `object` == view
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val banner  = ImageView(mContext);
        banner.scaleType = ImageView.ScaleType.CENTER_CROP;
        banner.background = ContextCompat.getDrawable(mContext, R.drawable.rounded_corner_layout)
        banner.clipToOutline = true;

        Picasso.get().load(mList[position].getThumb()).into(banner);
        banner.setOnClickListener {
            listener.onClick(mList[position], position)
        }

        container.addView(banner);

        return banner
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ImageView);
    }
}