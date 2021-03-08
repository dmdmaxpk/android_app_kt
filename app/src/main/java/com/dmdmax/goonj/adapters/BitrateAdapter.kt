package com.dmdmax.goonj.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.dmdmax.goonj.R
import com.dmdmax.goonj.models.BitRatesModel
import java.util.*

class BitrateAdapter: BaseAdapter {
    private var list: ArrayList<BitRatesModel>? = null
    private var context: Context? = null
    private var currentSelected = 0;

    interface OnBitrateItemClickListener {
        fun onClick(model: BitRatesModel?, position: Int)
    }

    constructor(list: ArrayList<BitRatesModel>?, context: Context?) {
        this.list = list
        this.context = context
    }

    internal class ViewHolder(view: View?) {
        val bitrate: TextView

        init {
            bitrate = view!!.findViewById(R.id.bitrate)
        }
    }

    override fun getCount(): Int {
        return list!!.size
    }

    override fun getItem(position: Int): Any? {
        return list!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.bitrate_list_item, parent, false)
            holder = ViewHolder(convertView)
            convertView!!.setTag(holder)
        } else {
            holder = convertView.tag as ViewHolder
        }
        if (list!![position].isSelected()) {
            currentSelected = position;
            holder.bitrate.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            holder.bitrate.setBackgroundResource(R.drawable.selected_bitrate_background)
        } else {
            holder.bitrate.setTextColor(ContextCompat.getColor(context!!, R.color.cloudy_gray))
            holder.bitrate.setBackgroundResource(R.drawable.unselected_bitrate_background)
        }
        holder.bitrate.text = list!![position].getBitrate()
        return convertView
    }

    public fun getCurrentSelected(): Int{
        return currentSelected;
    }
}