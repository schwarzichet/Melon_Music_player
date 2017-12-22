package com.example.dfz.myapplication;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.dfz.myapplication.MUtils.ColorUtils;
import com.example.dfz.myapplication.MUtils.SongUtil;
import com.example.dfz.myapplication.MUtils.TimeFormat;
import com.example.dfz.myapplication.Model.Song;
import com.example.dfz.myapplication.Service.MusicService;

import java.util.Collections;

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
    public static boolean isVisible = false;

    private MusicService myService;
    boolean mBound = false;

    private GestureDetectorCompat mDetector;

    public static Handler handler;


    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

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
                    Log.d(TAG, "handleMessage: nextsong" + song);

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

//        updateColor(albumImageView.getDrawingCache());
//        Glide.with(this)
//                .load()
//                .centerCrop()
//                .into(yourView);
//        Glide.with(this).load(imageUri).centerCrop().into();
        Glide.with(this).asBitmap().load(imageUri).into(new SimpleTarget<Bitmap>(200, 200) {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                createPaletteAsync(resource);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

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

        Glide.with(this).asBitmap().load(imageUri).into(new SimpleTarget<Bitmap>(200, 200) {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                createPaletteAsync(resource);
            }
        });

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
        previousSong = findViewById(R.id.previous_song);
        switchMode = findViewById(R.id.switch_mode);
        moreOperation = findViewById(R.id.more_operation);

        if (isPlaying)
            controlBarPlay.setImageResource(R.drawable.ic_pause);
        else
            controlBarPlay.setImageResource(R.drawable.ic_play_arrow);

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
    }


    @SuppressLint("HandlerLeak")
    @Override
    public void onStart() {
        super.onStart();
        isVisible = true;
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
                myService.playNext();

            }
        });

        previousSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPlaying = true;
                controlBarPlay.setImageResource(R.drawable.ic_pause);
                myService.playPrevious();
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

//        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

//        View view = findViewById(R.id.player_layout);
//        view.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return mDetector.onTouchEvent(motionEvent);
//            }
//        });

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return mDetector.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(TAG, "onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            Log.d(TAG, "onFling: " + e1.toString() + e2.toString());
            if (velocityY < 0 && Math.abs(velocityY) > Math.abs(velocityX)) {
                finish();
            }
            if (Math.abs(velocityY) < Math.abs(velocityX)) {
                if (velocityX < 0) {
                    isPlaying = true;
                    controlBarPlay.setImageResource(R.drawable.ic_pause);
                    myService.playNext();
                } else {
                    isPlaying = true;
                    controlBarPlay.setImageResource(R.drawable.ic_pause);
                    myService.playPrevious();
                }
            }

            return true;
        }
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

    private void updateColor(Bitmap bitmap) {
        // Generate the palette and get the vibrant swatch
        // See the createPaletteSync() and checkVibrantSwatch() methods
        // from the code snippets above
        Palette p = createPaletteSync(bitmap);
        Palette.Swatch vibrantSwatch = checkVibrantSwatch(p);


        title.setTextColor(vibrantSwatch.getRgb());
        artist.setTextColor(vibrantSwatch.getRgb());


    }


    private Palette.Swatch checkVibrantSwatch(Palette palette) {
        if (palette != null) {
            if (palette.getVibrantSwatch() != null) {
                return palette.getVibrantSwatch();
            } else if (palette.getMutedSwatch() != null) {
                return palette.getMutedSwatch();
            } else if (palette.getDarkVibrantSwatch() != null) {
                return palette.getDarkVibrantSwatch();
            } else if (palette.getDarkMutedSwatch() != null) {
                return palette.getDarkMutedSwatch();
            } else if (palette.getLightVibrantSwatch() != null) {
                return palette.getLightVibrantSwatch();
            } else if (palette.getLightMutedSwatch() != null) {
                return palette.getLightMutedSwatch();
            }
        }
        return new Palette.Swatch(0, 0);
    }

    // Generate palette synchronously and return it
    public Palette createPaletteSync(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        return p;
    }

    // Generate palette asynchronously and use it on a different
    // thread using onGenerated()
    public void createPaletteAsync(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                // Use generated instance
                Palette.Swatch vibrantSwatch = checkVibrantSwatch(p);

                // Set the toolbar background and text colors
                if (vibrantSwatch == null) {
                    Log.d(TAG, "onGenerated: no virbrantSwatch");
                }
                Log.d(TAG, "onGenerated: " + vibrantSwatch.getRgb());
                progressBar.getProgressDrawable().setColorFilter(
                        vibrantSwatch.getRgb(), android.graphics.PorterDuff.Mode.SRC_IN);
//                progressBar.setProgressTintList(vibrantSwatch.getRgb());

                int rgb = vibrantSwatch.getRgb();

//                float[] hsv = new float[3];
//                int color = Color.alpha(rgb);
//                Color.colorToHSV(color, hsv);
//                hsv[2] *= 0.8f; // value component
                int color = ColorUtils.darken(rgb, 0.3);
                View view = findViewById(R.id.player_layout);
                view.setBackgroundColor(color);

                progressBar.setDrawingCacheBackgroundColor(Color.WHITE);
                title.setTextColor(Color.WHITE);
                artist.setTextColor(Color.WHITE);
                currentTime.setTextColor(Color.WHITE);
                endTime.setTextColor(Color.WHITE);
                controlBarPlay.setColorFilter(Color.WHITE);
                previousSong.setColorFilter(Color.WHITE);
                nextSong.setColorFilter(Color.WHITE);
                switchMode.setColorFilter(Color.WHITE);
                moreOperation.setColorFilter(Color.WHITE);
            }
        });
    }

}
