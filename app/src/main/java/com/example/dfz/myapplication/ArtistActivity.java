package com.example.dfz.myapplication;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dfz.myapplication.MUtils.ArtistLoader;
import com.example.dfz.myapplication.MUtils.ArtistUtil;
import com.example.dfz.myapplication.Model.Album;
import com.example.dfz.myapplication.Model.Artist;
import com.example.dfz.myapplication.Model.Song;
import com.example.dfz.myapplication.Service.MusicService;

import static android.widget.Toast.makeText;

public class ArtistActivity extends AppCompatActivity implements LowerBar.LowerBarFragmentTouchListener, LowerBar.LowerBarPlayButtonClickListener, LowerBar.LowerBarNextButtonClickListener{

    private static final String TAG = "ArtistActivity";

    public static boolean isVisible = false;

    private long currentTimeMs = 0;
    private boolean isPlaying = false;

    private MusicService myService;
    boolean mBound = false;

    public static final int MSG_NEXT_SONG = 1;
    private static Handler handler;
    public static Messenger messenger;

    private class ArtistActivityHandler extends Handler {
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
        setContentView(R.layout.activity_artist);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Artist");
        }

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "onCreate: bind service : mBound"+mBound);
        handler = new ArtistActivityHandler();
        messenger = new Messenger(handler);

        Intent intent1 = getIntent();
        int artist_id = intent1.getIntExtra("artist_id", -1);
        if (artist_id!=-1){
            Artist a = ArtistLoader.getArtist(getBaseContext(), artist_id);
            TextView artistName = findViewById(R.id.artist_name_artist_activity);
            artistName.setText(a.getName());

            ImageView artistImage = findViewById(R.id.artist_image_artist_activity);
            ArtistUtil.setArtistImage(this, a.getName(), a.getId(), artistImage, 3);

            RecyclerView mRecycleView = findViewById(R.id.artist_albums);
            mRecycleView.setLayoutManager(new GridLayoutManager(this, 3));
            MyAlbumAdapter mAlbumAdapter = new MyAlbumAdapter(this, a.albums);
            mAlbumAdapter.setOnItemClickListener((itemView, pos) -> {

                Album album = a.albums.get(pos);

                Intent intent2 = new Intent(this, AlbumActivity.class);
                intent2.putExtra("albumId", album.getId());
                Log.d(TAG, "success!");
                startActivity(intent2);
            });
            mRecycleView.setAdapter(mAlbumAdapter);

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
            Log.d(TAG, "onResume: yes bound");
            updateFragment();
        }else {
            Log.d(TAG, "onResume: oh, not bound?");

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
            Log.d(TAG, "onServiceConnected: mBound: "+mBound);

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
                //getFragmentManager().popBackStack();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.container_in_artist_activity, lowerBar).addToBackStack(null).commit();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "onServiceDisConnected: ");
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
        isPlaying = myService.isPlaying();
        Bundle bundle = new Bundle();
        bundle.putString("title", s.getTitle());
        bundle.putString("artist", s.getArtist());
        bundle.putInt("albumId", s.getAlbumID());
        bundle.putLong("duration", s.getDuration());
        bundle.putBoolean("isPause", !myService.isPlaying());

        LowerBar lowerBar = new LowerBar();
        lowerBar.setArguments(bundle);
        getFragmentManager().popBackStack();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_in_artist_activity, lowerBar).addToBackStack(null).commit();
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
