package com.example.dfz.myapplication;

import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dfz.myapplication.MUtils.PlayListLoader;
import com.example.dfz.myapplication.MUtils.PlayListUtil;
import com.example.dfz.myapplication.MUtils.TimeFormat;
import com.example.dfz.myapplication.Model.PlayList;
import com.example.dfz.myapplication.Model.PlayListSong;
import com.example.dfz.myapplication.Model.Song;
import com.example.dfz.myapplication.Service.MusicService;

import java.util.ArrayList;
import java.util.Collections;

import static android.widget.Toast.makeText;

public class PlayListActivity extends AppCompatActivity implements LowerBar.LowerBarFragmentTouchListener, LowerBar.LowerBarPlayButtonClickListener, LowerBar.LowerBarNextButtonClickListener  {

    private static final String TAG = "PlayListActivity";


    public static boolean isVisible = false;

    protected boolean isPlaying = false;

    protected MusicService myService;
    protected boolean mBound = false;

    public static final int MSG_NEXT_SONG = 1;

    protected static Handler handler;
    public static Messenger messenger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);


        handler = new PlayListActivityHandler();
        messenger = new Messenger(handler);
        Intent intentService = new Intent(this, MusicService.class);
        bindService(intentService, mConnection, Context.BIND_AUTO_CREATE);


        Intent intent = getIntent();
        int playlist_id = intent.getIntExtra("playlist_id", -1);
        if (playlist_id != -1) {
            Log.d(TAG, "onCreate: valid playlist");
            PlayList p = PlayListLoader.getPlayList(this, playlist_id);

            ArrayList<PlayListSong> playListSongs = PlayListUtil.getPlayListSongList(this, playlist_id);

            int songNum = playListSongs.size();

            long duration = 0;
            ArrayList<Song> songs = new ArrayList<>();
            for (PlayListSong ps : playListSongs) {
                duration += ps.getDuration();
                songs.add(ps);
            }

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(p.name);
            }
            TextView playListStat = findViewById(R.id.playlist_stat);
            TimeFormat tf = new TimeFormat(duration);
            playListStat.setText(songNum+" songs - "+ tf.toTimeFormat());
            Collections.sort(playListSongs, (song1, song2) -> song1.idInPlayList-song2.idInPlayList);



            RecyclerView mRecyclerView = findViewById(R.id.playlist_activity_list);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            MySongAdapter mySongAdapter = new MySongAdapter(this, songs);
            mySongAdapter.setOnItemClickListener(new MySongAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int pos) {

                    isPlaying = true;
                    Bundle bundle = new Bundle();
                    Song s = songs.get(pos);
                    bundle.putString("title", s.getTitle());
                    bundle.putString("artist", s.getArtist());
                    bundle.putInt("albumId", s.getAlbumID());
                    bundle.putLong("duration", s.getDuration());
                    bundle.putBoolean("isPause", !isPlaying);

                    LowerBar lowerBar = new LowerBar();
                    lowerBar.setArguments(bundle);
                    //getFragmentManager().popBackStack();
                    if(getFragmentManager().getBackStackEntryCount()>0) {
                        getFragmentManager().popBackStack();
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.lowerbar_container, lowerBar).addToBackStack(null).commit();
                    }
                    else {
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.add(R.id.lowerbar_container, lowerBar).addToBackStack(null).commit();
                    }

                    if (mBound) {
                        myService.playSong(s);
                    }
                }

                @Override
                public void onItemLongClick(View itemView, int pos) {

                }
            });
            mRecyclerView.setAdapter(mySongAdapter);

        }

    }

    private class PlayListActivityHandler extends Handler {
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
