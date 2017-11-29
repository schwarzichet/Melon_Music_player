package com.example.dfz.myapplication.MUtils;

import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

/**
 * Created by hp on 2017/11/29.
 */

public class PlayerUtil {
//    private void initializePlayer(SimpleExoPlayer player) {
//        player = ExoPlayerFactory.newSimpleInstance(
//                new DefaultRenderersFactory(this),
//                new DefaultTrackSelector(), new DefaultLoadControl());
//
//        playerView.setPlayer(player);
//
//        player.setPlayWhenReady(playWhenReady);
//        player.seekTo(currentWindow, playbackPosition);
//
//        Uri uri = Uri.parse(songUri);
//        Log.d(TAG, "initializePlayer: uri = " + uri);
//        MediaSource mediaSource = buildMediaSource(uri);
//        player.prepare(mediaSource, true, false);
//    }
//
//    private void releasePlayer() {
//        if (player != null) {
//            playbackPosition = player.getCurrentPosition();
//            currentWindow = player.getCurrentWindowIndex();
//            playWhenReady = player.getPlayWhenReady();
//            player.release();
//            player = null;
//        }
//    }
}
