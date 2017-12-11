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
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dfz.myapplication.MUtils.SongUtil;
import com.example.dfz.myapplication.MUtils.TimeFormat;
import com.example.dfz.myapplication.Model.Song;
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

    private ImageButton controlBarPlay;
    private ImageButton previousSong;
    private ImageButton nextSong;
    private ImageButton switchMode;
    private ImageButton moreOperation;

    private boolean isPlaying = false;

    private MusicService myService;
    boolean mBound = false;

    public static Handler handler;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    public static Messenger messenger;
    public final static int UPDATE_PROGRESS = 1;
    public final static int NEXT_SONG = 2;

    private class playerActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage: yes i recieve msg from music service");
            if (PlayerActivity.this.isDestroyed()) {
                return;
            }
            switch (msg.what) {
                case UPDATE_PROGRESS:

                    Bundle bundle = msg.getData();
                    long currentPosition = bundle.getLong("currentPosition");
                    TimeFormat current = new TimeFormat(currentPosition);
                    String currentTimef = current.toTimeFormat();
                    currentTime.setText(currentTimef);
                    int progress = (int) Math.round((double) currentPosition / durationMs * progressBarMax);
                    progressBar.setProgress(progress);
                    break;
                case NEXT_SONG:
                    Song song = (Song) msg.obj;
                    updateView(song);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void updateView(Song song) {
        Uri imageUri = SongUtil.getAlbumArt(song.getAlbumID());
        Glide.with(this).load(imageUri).into(albumImageView);

        title.setText(song.getTitle());
        artist.setText(song.getArtist());

        durationMs = song.getDuration();
        TimeFormat durationTimeFormat = new TimeFormat(song.getDuration());
        String durationString = durationTimeFormat.toTimeFormat();
        endTime.setText(durationString);
    }

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
        long currentMs = intent.getLongExtra("currentTimeMs", 0);
        isPlaying = intent.getBooleanExtra("isPlaying", false);

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

        controlBarPlay = findViewById(R.id.control_bar_play);
        previousSong = findViewById(R.id.previous_song);
        nextSong = findViewById(R.id.next_song);
        switchMode = findViewById(R.id.switch_mode);
        moreOperation = findViewById(R.id.more_operation);

        if (isPlaying)
            controlBarPlay.setImageResource(R.drawable.ic_pause);
        else
            controlBarPlay.setImageResource(R.drawable.ic_play_arrow);

    }


    @SuppressLint("HandlerLeak")
    @Override
    public void onStart() {
        super.onStart();

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


        handler = new playerActivityHandler();

        messenger = new Messenger(handler);


        controlBarPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    myService.pause();
                    controlBarPlay.setImageResource(R.drawable.ic_play_arrow);
                } else {
                    myService.start();
                    controlBarPlay.setImageResource(R.drawable.ic_pause);
                }
                isPlaying = !isPlaying;
            }
        });

        nextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPlaying = true;
                controlBarPlay.setImageResource(R.drawable.ic_pause);

                //
            }
        });

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
