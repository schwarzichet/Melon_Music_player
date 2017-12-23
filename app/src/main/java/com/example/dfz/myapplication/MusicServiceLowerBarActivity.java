package com.example.dfz.myapplication;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.dfz.myapplication.Model.Song;
import com.example.dfz.myapplication.Service.MusicService;

import static android.widget.Toast.makeText;

/**
 * Created by DFZ on 2017/12/23.
 */

@SuppressLint("Registered")
public class MusicServiceLowerBarActivity extends AppCompatActivity implements LowerBar.LowerBarFragmentTouchListener, LowerBar.LowerBarPlayButtonClickListener, LowerBar.LowerBarNextButtonClickListener {
    public static boolean isVisible = false;

    protected boolean isPlaying = false;

    protected MusicService myService;
    protected boolean mBound = false;

    public static final int MSG_NEXT_SONG = 1;

    protected static Handler handler;
    public static Messenger messenger;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new MusicServiceLowerBarActivityHandler();
        messenger = new Messenger(handler);
        Intent intentService = new Intent(this, MusicService.class);
        bindService(intentService, mConnection, Context.BIND_AUTO_CREATE);
    }




    private class MusicServiceLowerBarActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (isDestroyed()) {
                return;
            }
            switch (msg.what) {
                case MSG_NEXT_SONG:
                    updateFragment();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        if (mBound) {
            updateFragment();
        } else {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
        }
        mBound = false;
        handler.removeCallbacksAndMessages(null);
    }

    public ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MusicService.MyBinder binder = (MusicService.MyBinder) service;
            myService = binder.getService();
            mBound = true;
//            Log.d(TAG, "onServiceConnected: mBound: " + mBound);

            if (myService.nowPlaySong() != null) {
                Log.d("show lowerbar", "show");
                Song nowSong = myService.nowPlaySong();
                Bundle bundle = new Bundle();
                bundle.putString("title", nowSong.getTitle());
                bundle.putString("artist", nowSong.getArtist());
                bundle.putInt("albumId", nowSong.getAlbumID());
                bundle.putLong("duration", nowSong.getDuration());
                bundle.putBoolean("isPause", myService.isPause);

                LowerBar lowerBar = new LowerBar();
                lowerBar.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.lowerbar_container, lowerBar).addToBackStack(null).commit();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void goToPlayer(Bundle bundle) {
        Song s = myService.nowPlaySong();
        long currentTimeMs = bundle.getLong("currentMs");
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("albumId", s.getAlbumID());
        intent.putExtra("title", s.getTitle());
        intent.putExtra("artist", s.getArtist());
        intent.putExtra("duration", s.getDuration());
        intent.putExtra("currentTimeMs", currentTimeMs);
        intent.putExtra("isPlaying", isPlaying);
        startActivity(intent);
    }

    @Override
    public void playOrPause() {

        if (isPlaying) {
            myService.pause();
        } else {
            myService.start();
        }
        isPlaying = !isPlaying;
    }

    @Override
    public void nextSong() {
        myService.playNext();

        updateFragment();


        isPlaying = true;
    }

    private void updateFragment() {
        Song s = myService.nowPlaySong();
        makeText(getBaseContext(), "now Song is" + s, Toast.LENGTH_SHORT).show();

        Bundle bundle = new Bundle();
        bundle.putString("title", s.getTitle());
        bundle.putString("artist", s.getArtist());
        bundle.putInt("albumId", s.getAlbumID());
        bundle.putLong("duration", s.getDuration());
        bundle.putBoolean("isPause", false);

        LowerBar lowerBar = new LowerBar();
        lowerBar.setArguments(bundle);
        getFragmentManager().popBackStack();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.lowerbar_container, lowerBar).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
