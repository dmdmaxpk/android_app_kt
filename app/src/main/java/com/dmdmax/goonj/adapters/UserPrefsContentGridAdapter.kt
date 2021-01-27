package com.dmdmax.goonj.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.network.responses.Category
import com.dmdmax.goonj.storage.GoonjPrefs
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserPrefsContentGridAdapter: BaseAdapter {
    private var context: Context? = null
    private var list: ArrayList<Category>
    private var mPrefs: GoonjPrefs? = null
    private var mContext: Context

    constructor(context: Context, objects: ArrayList<Category>) {
        this.context = context
        list = objects
        this.mContext = context;
        mPrefs = GoonjPrefs(context)
    }

    class LiveViewHolder internal constructor(view: View) {
        val thumbnail: CircleImageView = view.findViewById(R.id.thumb)
        val category: TextView = view.findViewById(R.id.category)
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(i: Int): Category {
        return list[i];
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: LiveViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_content_prefs_grid_layout, parent, false)
            holder = LiveViewHolder(convertView)
            convertView!!.setTag(holder)
        } else {
            holder = convertView.tag as LiveViewHolder
        }

        val item: Category = getItem(position);
        if(item.isSelected()){
            Picasso.get().load(R.drawable.category_selected_bg).into(holder.thumbnail);
            holder.category.setTextColor(Color.WHITE)
        }else{
            Picasso.get().load(R.drawable.category_not_selected_bg).into(holder.thumbnail);
            holder.category.setTextColor(mContext.getColor(R.color.cloudy_gray))
        }

        holder.category.text = item.getName().capitalize();
        return convertView
    }
}