package com.dmdmax.goonj.screens.implements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.dmdmax.goonj.R
import com.dmdmax.goonj.adapters.BottomGridAdapter
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.models.BottomMenu
import com.dmdmax.goonj.screens.views.WelcomeView
import androidx.core.content.ContextCompat.startActivity

import android.content.ActivityNotFoundException

import android.content.Intent
import android.net.Uri
import com.dmdmax.goonj.utility.Constants


class Welcomelmpl: BaseObservableView<WelcomeView.Listener>, WelcomeView, View.OnClickListener {

    private lateinit var mContentFrameLayout: FrameLayout;
    private lateinit var mHeaderLayout: FrameLayout;
    private lateinit var mFooterLayout: LinearLayout;
    private lateinit var mBottomGrid: GridView;
    private lateinit var mUser: ImageView;
    private lateinit var mSearch: ImageView;

    private lateinit var mList: ArrayList<BottomMenu>;
    private lateinit var mAdapter: BottomGridAdapter;

    private lateinit var mAdLayout: LinearLayout;
    private lateinit var mCross: ImageView;
    private lateinit var mAdImage: ImageView;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.activity_welcome, parent, false));
    }

    override fun initialize() {
        mContentFrameLayout = findViewById(R.id.content_frame);
        mBottomGrid = findViewById(R.id.bottom_grid);
        mHeaderLayout = findViewById(R.id.header_layout);
        mFooterLayout = findViewById(R.id.footer_layout);
        mAdLayout = findViewById(R.id.adLayout);
        mCross = findViewById(R.id.cross); mCross.setOnClickListener(this);
        mAdImage = findViewById(R.id.ad_image); mAdImage.setOnClickListener(this);

        mUser = findViewById(R.id.user); mUser.setOnClickListener(this);
        mSearch = findViewById(R.id.search); mSearch.setOnClickListener(this);

        if(!Constants.YOUTUBE_CHANNEL_SUBSCRIBE_AD){
            mAdLayout.visibility = View.GONE;
        }
    }


    override fun bindBottomAdapter() {
        mList = arrayListOf();
        mList.add(BottomMenu("Home", R.drawable.btm_home_not_focus, R.drawable.btm_home_not_focus, false));
        mList.add(BottomMenu("Live TV", R.drawable.btm_live_not_focus, R.drawable.btm_live_not_focus, false))
        mList.add(BottomMenu("VOD", R.drawable.btm_vod_not_focus, R.drawable.btm_vod_not_focus, false))
        //mList.add(BottomMenu("Favourite", R.drawable.btm_fvt_not_focus, R.drawable.btm_fvt_not_focus, false))
        mList.add(BottomMenu("More", R.drawable.btm_more_not_focus, R.drawable.btm_more_not_focus, false))

        mBottomGrid.numColumns = mList.size;

        mAdapter = BottomGridAdapter(getContext(), mList, 0);
        mBottomGrid.adapter = mAdapter;

        mBottomGrid.onItemClickListener = object: AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mAdapter.markSelection(position);
                for (listener in getListeners()) {
                    listener.onBottomClick(position);
                }
            }
        }
    }

    override fun setCurrentBottomIndex(position: Int) {
        mAdapter.markSelection(position)
    }

    override fun setFullScreen(isFull: Boolean) {
        if(isFull){
            mHeaderLayout.visibility = View.GONE;
            mFooterLayout.visibility = View.GONE;
        }else{
            mHeaderLayout.visibility = View.VISIBLE;
            mFooterLayout.visibility = View.VISIBLE;
        }
    }

    override fun currentBottomIndex(): Int {
        return mAdapter.getCurrentSelection();
    }

    override fun onClick(v: View) {
        when(v){
            mUser -> {
                for (listener in getListeners()) {
                    listener.onUserClick();
                }
            }

            mSearch -> {
                for (listener in getListeners()) {
                    listener.onSearchClick();
                }
            }

            mCross -> {
                mAdLayout.visibility = View.GONE;
            }

            mAdImage -> {
                mAdLayout.visibility = View.GONE;

                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCE126WZCUfLqOpcxRo55KYg"))
                try {
                    getContext().startActivity(webIntent)
                } catch (ex: ActivityNotFoundException) {
                    ex.printStackTrace()
                }
            }
        }
    }
}