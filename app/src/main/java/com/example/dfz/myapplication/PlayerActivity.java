package com.example.dfz.myapplication;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dfz.myapplication.MUtils.SongUtil;
import com.example.dfz.myapplication.Model.TimeFormat;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.exoplayer2.C.TIME_UNSET;

public class PlayerActivity extends AppCompatActivity {

    private static final String TAG = "PLAYERACTIVITY";
    private String songUri;
    private SimpleExoPlayer player;
//    private SimpleExoPlayerView playerView;

    private static Handler handler;

    private ImageView albumImageView;
    private SeekBar progressBar;
    private TextView startTime;
    private TextView endTime;
    private long durationMs;
    private int progressBarMax;
    private long currentPosition;

    private TextView title;
    private TextView artist;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false);      // Disable the button
            actionBar.setDisplayHomeAsUpEnabled(false); // Remove the left caret
            actionBar.setDisplayShowHomeEnabled(false); // Remove the icon
        }else {
            Log.d(TAG, "onCreate: no actionbar");
        }
//        setContentView(R.layout.activity_player);

        setContentView(R.layout.player_layout);

        Intent intent = getIntent();
        songUri = "file://" + intent.getStringExtra("SongUri");

        albumImageView = findViewById(R.id.albumCover);
        title = (TextView)findViewById(R.id.current_song_name);
        artist = (TextView)findViewById((R.id.current_song_by));
        int albumId = intent.getIntExtra("albumId", 0);
        String currentSongName = intent.getStringExtra("title");
        String currentSongBy = intent.getStringExtra("artist");
        durationMs = intent.getLongExtra("Duration", 0);

        Uri imageUri = SongUtil.getAlbumArt(albumId);
        Glide.with(this).load(imageUri).into(albumImageView);

        title.setText(currentSongName);
        artist.setText(currentSongBy);
        String song = (String) title.getText();
        Log.d("song", song);

        progressBar = (SeekBar)findViewById(R.id.seekbar);
        progressBarMax = progressBar.getMax();
        TimeFormat durationTimeFormat = new TimeFormat(durationMs);
        String duration = durationTimeFormat.toTimeFormat();
        Log.d("durationTimeFormat", duration);
        startTime = (TextView)findViewById(R.id.start_time);
        endTime = (TextView)findViewById(R.id.end_time);
        startTime.setText("00:00");
        endTime.setText(duration);

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = progressBar.getProgress();
                long currentPosition = (long) ((double)progress/progressBarMax*durationMs);
                player.seekTo(currentPosition);
            }
        });

        // Capture the layout's TextView and set the string as its text
//        playerView = (SimpleExoPlayerView) findViewById(R.id.audio_view);

//        albumImageView = findViewById(R.id.exo_artwork);



//        albumImageView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction()==MotionEvent.)
//                return false;
//            }
//        });


    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();

            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Bundle bundle = msg.getData();
                    long currentPosition = bundle.getLong("currentPosition");
                    int progress = (int) Math.round((double)currentPosition/durationMs*progressBarMax);
                    progressBar.setProgress(progress);
                }
            };
            updateProgress();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

//    @SuppressLint("InlinedApi")
//    private void hideSystemUi() {
//        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());

//        playerView.setPlayer(player);

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);

        Uri uri = Uri.parse(songUri);
        Log.d(TAG, "initializePlayer: uri = " + uri);
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);
//        playerView.setUseArtwork(true);
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

    private void updateProgress() {
        final Timer timer = new Timer();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                long currentPosition = player.getCurrentPosition();
                //Log.d("cur", currentPosition+"");
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putLong("currentPosition", currentPosition);
                //Log.d("bundle", "ok");

                msg.setData(bundle);
                handler.sendMessage(msg);
                //Log.d("send", "ok");
            }
        };
        timer.schedule(timerTask,0,500);
        //Log.d("sche", "ok");
    }

}
