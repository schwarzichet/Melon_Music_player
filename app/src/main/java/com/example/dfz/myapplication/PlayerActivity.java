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
import android.os.RemoteException;
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
    private TextView startTime;
    private TextView endTime;
    private long durationMs;

    private TextView title;
    private TextView artist;

    private Messenger myService = null;
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

        progressBar = findViewById(R.id.seekbar);
        progressBarMax = progressBar.getMax();


        albumImageView = findViewById(R.id.albumCover);
        title = (TextView) findViewById(R.id.current_song_name);
        artist = (TextView) findViewById((R.id.current_song_by));
        Intent intent = getIntent();
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


        TimeFormat durationTimeFormat = new TimeFormat(durationMs);
        String duration = durationTimeFormat.toTimeFormat();
        Log.d("durationTimeFormat", duration);
        startTime = (TextView) findViewById(R.id.start_time);
        endTime = (TextView) findViewById(R.id.end_time);
        startTime.setText("00:00");
        endTime.setText(duration);
    }


    @SuppressLint("HandlerLeak")
    @Override
    public void onStart() {
        super.onStart();

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "handleMessage: yes i recieve msg from music service");
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                long currentPosition = bundle.getLong("currentPosition");
                TimeFormat current = new TimeFormat(currentPosition);
                String currentTime = current.toTimeFormat();
                startTime.setText(currentTime);
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
                Message msg = Message.obtain(null, MusicService.DRAW_MUSIC_POSITION, currentPosition);
                try {
                    myService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
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
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            myService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            myService = null;
            mBound = false;
        }
    };


}
