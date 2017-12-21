package com.example.dfz.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dfz.myapplication.MUtils.AlbumLoader;
import com.example.dfz.myapplication.MUtils.SongLoader;
import com.example.dfz.myapplication.Model.Album;
import com.example.dfz.myapplication.Model.Song;
import com.example.dfz.myapplication.Service.MusicService;

import java.util.ArrayList;

import static android.widget.Toast.makeText;

public class AlbumActivity extends AppCompatActivity implements LowerBar.LowerBarFragmentTouchListener, LowerBar.LowerBarPlayButtonClickListener, LowerBar.LowerBarNextButtonClickListener{
    private RecyclerView mRecyclerView;
    private MySongAdapter mAdapter;
    private Album album;
    private ArrayList<Song> songs = new ArrayList<Song>();

    private long currentTimeMs = 0;
    private boolean isPlaying = false;

    private String TAG = "AlbumActivity";

    private MusicService myService;
    boolean mBound = false;

    android.app.FragmentManager fragmentManager;
    android.app.FragmentTransaction fragmentTransaction;

    public static boolean isVisible = true;

    public static final int MSG_NEXT_SONG = 1;
    private static Handler handler;
    public static Messenger messenger;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent2 = new Intent(this, MusicService.class);
        bindService(intent2, mConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "onCreate: bind service");
        handler = new AlbumActivityHandler();
        messenger = new Messenger(handler);
    }

    private class AlbumActivityHandler extends Handler {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_layout);

        Intent intent = getIntent();
        int albumId = intent.getIntExtra("albumId", 0);
        album = AlbumLoader.getAlbum(this, albumId);
        songs = album.songs;

        mRecyclerView = findViewById(R.id.songs_of_the_album);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new MySongAdapter(this, songs);
        mAdapter.setOnItemClickListener(new MySongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                isPlaying = true;
                Bundle bundle = new Bundle();
                Song s = songs.get(position);
                bundle.putString("title", s.getTitle());
                bundle.putString("artist", s.getArtist());
                bundle.putInt("albumId", s.getAlbumID());
                bundle.putLong("duration", s.getDuration());

                LowerBar lowerBar = new LowerBar();
                lowerBar.setArguments(bundle);
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.lower_bar, lowerBar);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                if (mBound) {
                    myService.playSong(s);
                }

            }

            @Override
            public void onItemLongClick(View itemView, int pos) {

            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }

    public ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MusicService.MyBinder binder = (MusicService.MyBinder) service;
            Log.d(TAG, "onServiceConnected: ");
            myService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void goToPlayer(Bundle bundle) {
        Song s = myService.nowPlaySong();
        currentTimeMs = bundle.getLong("currentMs");
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

        LowerBar lowerBar = new LowerBar();
        lowerBar.setArguments(bundle);
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStack();
        fragmentTransaction.replace(R.id.lower_bar, lowerBar);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
