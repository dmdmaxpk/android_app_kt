package com.dmdmax.goonj.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.dmdmax.goonj.R
import com.dmdmax.goonj.models.Category
import com.dmdmax.goonj.storage.DBHelper
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
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
        val tick: ImageView = view.findViewById(R.id.tick)
        val foreground: ImageView = view.findViewById(R.id.foreground)
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
        val link: String = Constants.ThumbnailManager.getIconThumbs(item.getName().toLowerCase(), DBHelper.Companion.Tags.TAG_CATEGORY);
        Logger.println("URL - "+link)
        Picasso.get().load(link).into(holder.thumbnail);
        holder.thumbnail.setBackgroundResource(R.drawable.category_not_selected_bg);

        if(item.isSelected()){
            holder.foreground.visibility = View.VISIBLE;
            holder.tick.visibility = View.VISIBLE;
            holder.category.setTextColor(Color.WHITE)
        }else{
            holder.category.setTextColor(ContextCompat.getColor(mContext, R.color.cloudy_gray));
            holder.tick.visibility = View.GONE;
            holder.foreground.visibility = View.GONE;
        }

        holder.category.text = item.getName().capitalize();
        return convertView
    }
}