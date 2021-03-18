package com.dmdmax.goonj.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.dmdmax.goonj.R
import com.dmdmax.goonj.models.PaywallPackage
import com.dmdmax.goonj.storage.GoonjPrefs

class PackageListAdapter: BaseAdapter {

    private lateinit var mPackagesList: List<PaywallPackage>;
    private var slug: String;
    private var mContext: Context;
    private var mPrefs: GoonjPrefs;

    constructor(mPackagesList: List<PaywallPackage>, slug: String, context: Context) {
        this.mPackagesList = mPackagesList
        this.slug = slug
        this.mContext = context;
        this.mPrefs = GoonjPrefs(mContext);
    }

    override fun getCount(): Int {
        return mPackagesList!!.size
    }

    override fun getItem(i: Int): Any? {
        return mPackagesList!![i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(mContext).inflate(R.layout.settings_package_list_item_subscribe_now, parent, false);
        val packageName = view.findViewById<TextView>(R.id.package_name);
        val smallPb = view.findViewById<ProgressBar>(R.id.progress_bar);
        val subStatusLayout = view.findViewById<FrameLayout>(R.id.subs_status_layout);
        val subsText = view.findViewById<TextView>(R.id.subs_text);
        packageName.text = mPackagesList[position].getName();

        if(mPrefs.getSubscriptionStatus(slug).equals("subscribed")){
            if(mPrefs.getSubscribedPackageId(slug).equals(mPackagesList[position].getId())){
                smallPb.visibility = View.GONE;
                subsText.text = "Unsubscribe";
                subStatusLayout.visibility = View.VISIBLE;
                subStatusLayout.background = ContextCompat.getDrawable(mContext, R.drawable.unsubscribe);
                return view;
            }else{
                smallPb.visibility = View.GONE;
                subsText.text = "Subscribe Now";
                subStatusLayout.visibility = View.VISIBLE;
                subStatusLayout.background = ContextCompat.getDrawable(mContext, R.drawable.subscribe_now);
                return view;
            }
        }else{
            smallPb.visibility = View.GONE;
            subsText.text = "Subscribe Now";
            subStatusLayout.visibility = View.VISIBLE;
            subStatusLayout.background = ContextCompat.getDrawable(mContext, R.drawable.subscribe_now);
            return view;
        }
    }
}