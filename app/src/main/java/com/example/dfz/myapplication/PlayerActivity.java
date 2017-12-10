package com.example.dfz.myapplication;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dfz.myapplication.MUtils.SongUtil;
import com.example.dfz.myapplication.MUtils.TimeFormat;
import com.example.dfz.myapplication.Service.MusicService;

public class PlayerActivity extends AppCompatActivity {

    private static final String TAG = "PLAYERACTIVITY";
    private int progressBarMax = 0;

    private ImageView albumImageView;
    private SeekBar progressBar;
    private TextView currentTime;
    private TextView endTime;
    private long durationMs;

    private TextView title;
    private TextView artist;

    private MusicService myService;
    boolean mBound = false;

    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false);      // Disable the button
            actionBar.setDisplayHomeAsUpEnabled(false); // Remove the left caret
            actionBar.setDisplayShowHomeEnabled(false); // Remove the icon
        } else {
            Log.d(TAG, "onCreate: no actionbar");
        }

        setContentView(R.layout.player_layout);

        albumImageView = findViewById(R.id.albumCover);
        title = (TextView) findViewById(R.id.current_song_name);
        artist = (TextView) findViewById((R.id.current_song_by));

        progressBar = findViewById(R.id.seekbar);
        progressBarMax = progressBar.getMax();
        currentTime = (TextView) findViewById(R.id.start_time);
        endTime = (TextView) findViewById(R.id.end_time);

        Intent intent = getIntent();
        int albumId = intent.getIntExtra("albumId", 0);
        String currentSongName = intent.getStringExtra("title");
        String currentSongBy = intent.getStringExtra("artist");
        durationMs = intent.getLongExtra("duration", 0);

        Intent intentService = new Intent(this, MusicService.class);
        bindService(intentService, mConnection, Context.BIND_AUTO_CREATE);
        long currentMs = myService.getCurrentPosition();

        Uri imageUri = SongUtil.getAlbumArt(albumId);
        Glide.with(this).load(imageUri).into(albumImageView);

        title.setText(currentSongName);
        artist.setText(currentSongBy);

        TimeFormat durationTimeFormat = new TimeFormat(durationMs);
        String duration = durationTimeFormat.toTimeFormat();

        TimeFormat currentTimeFormat = new TimeFormat(currentMs);
        String current = currentTimeFormat.toTimeFormat();

        currentTime.setText(current);
        endTime.setText(duration);

        int progress = (int) Math.round((double) currentMs / durationMs * progressBarMax);
        progressBar.setProgress(progress);
    }


    @SuppressLint("HandlerLeak")
    @Override
    public void onStart() {
        super.onStart();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "handleMessage: yes i recieve msg from music service");
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                long currentPosition = bundle.getLong("currentPosition");
                TimeFormat current = new TimeFormat(currentPosition);
                String currentTimef = current.toTimeFormat();
                currentTime.setText(currentTimef);
                int progress = (int) Math.round((double) currentPosition / durationMs * progressBarMax);
                progressBar.setProgress(progress);
            }
        };


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
                long currentPosition = (long) ((double) progress / progressBarMax * durationMs);
                myService.seekToPosition(currentPosition);

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicService.MyBinder binder = (MusicService.MyBinder) service;
            myService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


}
