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
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.example.dfz.myapplication.MUtils.SongLoader;
import com.example.dfz.myapplication.MainActivity;
import com.example.dfz.myapplication.Model.Song;
import com.example.dfz.myapplication.PlayerActivity;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by DFZ on 2017/12/9.
 */

public class MusicService extends Service {
    private static final String TAG = "MusicService";

    private ArrayList<Song> songs = new ArrayList<>();


    private SimpleExoPlayer player;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;
    //    private ArrayList<MediaSource> mediaSources = new ArrayList<>();
    private int nowSongIndex;

    //    private String songUri;
    private long durationMs;

    private final Timer timer = new Timer();

    private final IBinder mBinder = new MyBinder();


    private boolean isNext = false;
    private boolean isPrevious = false;


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

        //load songs
        songs = SongLoader.loadSongs(this);
        Log.d(TAG, "onStartCommand: song number is " + songs.size());


        startForeground(1, n);

        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onHandleIntent: I am handle intent ");

//        songUri = intent.getStringExtra("songUri");
//        durationMs = intent.getLongExtra("duration", 0);

        new playback().execute();
        return START_STICKY;
    }

    private void createPlayer() {
        if (player == null) {
            Log.d(TAG, "createPlayer: no player now");
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(MusicService.this),
                    new DefaultTrackSelector(), new DefaultLoadControl());

            player.addListener(new Player.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    Log.d(TAG, "onPlayerStateChanged: 2333" + playbackState);
                    if (playbackState == Player.STATE_ENDED || isNext || isPrevious) {


                        Song song = songs.get(nowSongIndex);
                        durationMs = song.getDuration();
                        if (isPrevious){
                            Toast.makeText(getBaseContext(), "previous song", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getBaseContext(), "next song", Toast.LENGTH_SHORT).show();

                        }

                        Message msgPlayerActivity = Message.obtain(null, PlayerActivity.NEXT_SONG, song);

                        Message msgLowerBar = Message.obtain(null, MainActivity.MSG_NEXT_SONG, song);

                        try {
                            if (MainActivity.isVisible) {
                                MainActivity.messenger.send(msgLowerBar);
                            }
                            if (PlayerActivity.isVisible) {
                                PlayerActivity.messenger.send(msgPlayerActivity);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {

                }

                @Override
                public void onPositionDiscontinuity() {

                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }
            });
        }
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

    public Song nowPlaySong() {
        return this.songs.get(nowSongIndex);
    }


    private class playback extends AsyncTask<Void, SimpleExoPlayer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            createPlayer();
            return null;
        }
    }

    private void updateProgress() {

        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                long currentPosition = player.getCurrentPosition();
                Message msg = Message.obtain(null, PlayerActivity.UPDATE_PROGRESS);
                Bundle bundle = new Bundle();
                bundle.putLong("duration", durationMs);
                bundle.putLong("currentPosition", currentPosition);
                msg.setData(bundle);


                if (PlayerActivity.handler != null) {
                    Log.d("send", "ok");

                    try {
                        PlayerActivity.messenger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

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

//    public long getCurrentPosition() {
//        return player.getCurrentPosition();
//    }

    public void start() {
        if (player != null) {
            player.setPlayWhenReady(true);
            if (!this.playWhenReady) {
                this.playWhenReady = true;
            }
        }
    }

    public void pause() {
        if (player != null) {
            player.setPlayWhenReady(false);
            if (this.playWhenReady) {
                this.playWhenReady = false;
            }
        }
    }

    public void stop() {
        if (player != null) {
            player.stop();
        }
    }

    public void playSong(Song s) {

        int index = songs.indexOf(s);
        if (index != -1) {
            nowSongIndex = index;
        } else {
            songs.add(nowSongIndex, s);
        }
        durationMs = s.getDuration();
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(getMediaSource(s.getData()), true, false);
        updateProgress();
    }

    public void playNext() {

        if (nowSongIndex < this.songs.size()) {
            nowSongIndex++;

        } else {
            Toast.makeText(getBaseContext(), "no next song!", Toast.LENGTH_SHORT).show();
            nowSongIndex = 0;
        }
        Song s = this.songs.get(nowSongIndex);
        isNext = true;
        player.setPlayWhenReady(!playWhenReady);
        isNext = false;
        player.setPlayWhenReady(playWhenReady);
        Log.d(TAG, "playNext: " + nowSongIndex);
        playSong(s);
    }

    public void playPrevious() {
        if (nowSongIndex > 0) {
            nowSongIndex--;
        } else {
            Toast.makeText(getBaseContext(), "no previous song!", Toast.LENGTH_SHORT).show();
            nowSongIndex = 0;
        }
        Song s = this.songs.get(nowSongIndex);


        isPrevious = true;
        player.setPlayWhenReady(!playWhenReady);
        isPrevious = false;
        player.setPlayWhenReady(playWhenReady);
        playSong(s);
    }

    private MediaSource getMediaSource(String songUri) {
        songUri = "file://" + songUri;
        Uri uri = Uri.parse(songUri);
        Log.d(TAG, "prepare media: uri = " + uri);
        return buildMediaSource(uri);
    }


    public void setSingleCycle() {
        player.setRepeatMode(Player.REPEAT_MODE_ONE);

    }


}
