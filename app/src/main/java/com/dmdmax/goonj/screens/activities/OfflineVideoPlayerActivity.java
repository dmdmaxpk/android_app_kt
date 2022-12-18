package com.dmdmax.goonj.screens.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.dmdmax.goonj.R;
import com.dmdmax.goonj.player.ExoPlayerManager;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.io.File;

public class OfflineVideoPlayerActivity extends AppCompatActivity {

    private PlayerView mPlayer;
    private SimpleExoPlayer player;
    private String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_video_player);
        mPlayer = findViewById(R.id.offlinePlayer);

        if(getIntent().getExtras().containsKey("link")) {
            link = getIntent().getExtras().getString("link");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(player == null){
            play();
        }else{
            player.setPlayWhenReady(true);
        }
    }

    private void play() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(
                    this,
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());
        }

        mPlayer.setPlayer(player);

        MediaSource mediaSource = buildMediaSource(Uri.parse(String.valueOf(Uri.fromFile(new File(link)))));
        player.prepare(mediaSource, true, false);
        player.setPlayWhenReady(true);
    }

    private MediaSource buildMediaSource(Uri uri) {
        String userAgent = "goonj-offline";
        return new ExtractorMediaSource.Factory(
                new DefaultDataSourceFactory(this, userAgent)).
                createMediaSource(uri);
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.setPlayWhenReady(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }
}