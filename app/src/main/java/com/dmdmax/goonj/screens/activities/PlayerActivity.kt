package com.dmdmax.goonj.screens.activities

import android.os.Bundle
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.screens.views.PlayerView

class PlayerActivity : BaseActivity(), PlayerView.Listener {

    companion object {
        var ARGS_ID = "arg_id";
        var ARGS_NAME = "arg_name";
        var ARGS_THUMBNAIL = "arg_thumbnail";
        var ARGS_HLS = "arg_hls";
        var ARGS_CHANNELS = "arg_channels";
    }

    private lateinit var mView: PlayerView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = getCompositionRoot().getViewFactory().getPlayerViewImpl(null);
        setContentView(mView.getRootView());
        init();
    }

    private fun init(){
        mView.initialize(intent.getStringExtra(ARGS_ID), intent.getStringExtra(ARGS_NAME), intent.getStringExtra(ARGS_THUMBNAIL), intent.getStringExtra(ARGS_HLS), intent.getSerializableExtra(ARGS_CHANNELS) as ArrayList<Channel>);
    }

    override fun onStart() {
        super.onStart();
        mView.registerListener(this);
    }

    override fun onStop() {
        super.onStop();
        mView.unregisterListener(this);
        mView.pauseStreaming();
    }

    override fun goBack() {
        onBackPressed()
    }
}