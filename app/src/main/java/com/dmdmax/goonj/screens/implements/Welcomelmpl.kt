package com.dmdmax.goonj.screens.implements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.ImageView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.adapters.BottomGridAdapter
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.network.responses.BottomMenu
import com.dmdmax.goonj.screens.views.WelcomeView

class Welcomelmpl: BaseObservableView<WelcomeView.Listener>, WelcomeView, View.OnClickListener {

    private lateinit var mContentFrameLayout: FrameLayout;
    private lateinit var mBottomGrid: GridView;
    private lateinit var mUser: ImageView;
    private lateinit var mSearch: ImageView;

    private lateinit var mList: ArrayList<BottomMenu>;
    private lateinit var mAdapter: BottomGridAdapter;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.activity_welcome, parent, false));
    }

    override fun initialize() {
        mContentFrameLayout = findViewById(R.id.content_frame);
        mBottomGrid = findViewById(R.id.bottom_grid);

        mUser = findViewById(R.id.user); mUser.setOnClickListener(this);
        mSearch = findViewById(R.id.search); mSearch.setOnClickListener(this);
    }

    override fun bindBottomAdapter() {
        mList = arrayListOf();
        mList.add(BottomMenu("Home", R.drawable.btm_home_not_focus, R.drawable.btm_home_not_focus, false));
        mList.add(BottomMenu("Live TV", R.drawable.btm_live_not_focus, R.drawable.btm_live_not_focus, false))
        mList.add(BottomMenu("VOD", R.drawable.btm_vod_not_focus, R.drawable.btm_vod_not_focus, false))
        mList.add(BottomMenu("Favourite", R.drawable.btm_fvt_not_focus, R.drawable.btm_fvt_not_focus, false))
        mList.add(BottomMenu("More", R.drawable.btm_more_not_focus, R.drawable.btm_more_not_focus, false))

        mAdapter = BottomGridAdapter(getContext(), mList, 0);
        mBottomGrid.adapter = mAdapter;

        mBottomGrid.onItemClickListener = object: AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mAdapter.markSelection(position);
            }
        }
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
        }
    }
}