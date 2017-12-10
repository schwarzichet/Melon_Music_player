package com.example.dfz.myapplication.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.dfz.myapplication.MainActivity;
import com.example.dfz.myapplication.PlayerActivity;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by DFZ on 2017/12/9.
 */

public class MusicService extends Service {
    private static final String TAG = "MusicService";


    private SimpleExoPlayer player;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;

    private String songUri;
    private long durationMs;

    private final Timer timer = new Timer();

    private final IBinder mBinder = new MyBinder();


    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "service destroy", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onDestroy: service destroy");
        releasePlayer();
        timer.cancel();
    }


    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification.Builder nb =
                new Notification.Builder(this)
                        .setContentTitle("testTitle")
                        .setContentText("testText")
                        .setContentIntent(pendingIntent)
                        .setTicker("testTicker");
        Notification n = nb.build();


        startForeground(1, n);

        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onHandleIntent: I am handle intent ");

        songUri = intent.getStringExtra("songUri");
        durationMs = intent.getLongExtra("duration", 0);

        new playback().execute(songUri);
        return START_STICKY;
    }

    private void createPlayer(String songUri) {
        if (player == null) {
            Log.d(TAG, "createPlayer: no player now");
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(MusicService.this),
                    new DefaultTrackSelector(), new DefaultLoadControl());
        }

    }

    public void setSong(String songUri){
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        this.songUri = "file://" + songUri;
        Uri uri = Uri.parse(this.songUri);
        Log.d(TAG, "createPlayer: uri = " + uri);
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);

        updateProgress();
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }


    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new FileDataSourceFactory(),
                new DefaultExtractorsFactory(), null, null);
    }

    private class playback extends AsyncTask<String, SimpleExoPlayer, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            createPlayer(strings[0]);
            return null;
        }
    }

    private void updateProgress() {

        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                long currentPosition = player.getCurrentPosition();
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putLong("duration", durationMs);
                bundle.putLong("currentPosition", currentPosition);
                msg.setData(bundle);


                if (PlayerActivity.handler != null) {
                    Log.d("send", "ok");
                    PlayerActivity.handler.sendMessage(msg);
                }
            }
        };
        timer.schedule(timerTask, 0, 500);
    }

    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }


    public void seekToPosition(long currentPosition) {
        player.seekTo(currentPosition);
    }

    public long getCurrentPosition() {
        return player.getCurrentPosition();
    }


}
