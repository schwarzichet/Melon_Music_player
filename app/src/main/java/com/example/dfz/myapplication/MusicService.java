package com.example.dfz.myapplication;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

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

    static final int DRAW_MUSIC_POSITION = 1;


    private SimpleExoPlayer player;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;

    private String songUri;
    private long durationMs;

    private Handler handler = new MyHandler();

    public MusicService() {

    }

    final Messenger mMessenger = new Messenger(new MyHandler());


    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "service destroy", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onDestroy: service destroy");
        releasePlayer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mMessenger.getBinder();
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

    private void initializePlayer(String songUri) {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(MusicService.this),
                    new DefaultTrackSelector(), new DefaultLoadControl());
        }


        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        songUri = "file://" + songUri;
        Uri uri = Uri.parse(songUri);
        Log.d(TAG, "initializePlayer: uri = " + uri);
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
            initializePlayer(strings[0]);
            return null;
        }
    }

    private void updateProgress() {
        final Timer timer = new Timer();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                long currentPosition = player.getCurrentPosition();
                //Log.d("cur", currentPosition+"");
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putLong("duration", durationMs);
                bundle.putLong("currentPosition", currentPosition);

                //Log.d("bundle", "ok");

                msg.setData(bundle);

                Log.d("send", "ok");
                if (PlayerActivity.handler!=null){
                    PlayerActivity.handler.sendMessage(msg);
                }
            }
        };
        timer.schedule(timerTask, 0, 500);
        //Log.d("sche", "ok");
    }


    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DRAW_MUSIC_POSITION:
                    long currentPosition = (long) msg.obj;
                    player.seekTo(currentPosition);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


}
