package com.dmdmax.goonj.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.dmdmax.goonj.R
import com.dmdmax.goonj.network.responses.BottomMenu


class BottomGridAdapter: BaseAdapter {

    private val mContext: Context;
    private val mList: List<BottomMenu>;
    private var mCurrentSelection: Int = 0;

    constructor(mContext: Context, mList: List<BottomMenu>, mDefaultSelection: Int) {
        this.mContext = mContext
        this.mList = mList
        this.mCurrentSelection = mDefaultSelection;

        unMarkAllIfAnyMarked(mList);
        markSelection(mCurrentSelection);
    }

    private fun unMarkAllIfAnyMarked(mList: List<BottomMenu>){
        for(mItem in mList){
            mItem.setFocused(false);
        }
    }

    fun markSelection(mCurrentSelection: Int){
        mList[this.mCurrentSelection].setFocused(false);
        mList[mCurrentSelection].setFocused(true);

        this.mCurrentSelection = mCurrentSelection;
        notifyDataSetChanged();
    }

    fun getCurrentSelection(): Int{
        return this.mCurrentSelection
    }

    override fun getCount(): Int {
        return mList.size;
    }

    override fun getItem(position: Int): BottomMenu {
        return mList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.bottom_menu_grid, parent, false)
        }

        val menuIcon = convertView!!.findViewById<ImageView>(R.id.menu_icon)
        val menuTitle = convertView.findViewById<TextView>(R.id.menu_title)
        menuTitle.text = mList[position].getTitle()


        menuIcon.setImageDrawable(getResource(mList[position].isFocused(), if (mList[position].isFocused()) mList[position].getFocusedImage() else mList[position].getNonFocusedImage()))
        menuTitle.setTextColor(if (mList[position].isFocused()) ContextCompat.getColor(mContext, R.color.white) else ContextCompat.getColor(mContext, R.color.grayed_out));
        return convertView
    }

    private fun getResource(isFocused: Boolean, mDrawable: Int): Drawable{
        val unwrappedDrawable = AppCompatResources.getDrawable(mContext, mDrawable)
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
        DrawableCompat.setTint(wrappedDrawable, if(isFocused) ContextCompat.getColor(mContext, R.color.white) else ContextCompat.getColor(mContext, R.color.grayed_out))
        return wrappedDrawable;
    }
}
