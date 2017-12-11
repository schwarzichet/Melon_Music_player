package com.example.dfz.myapplication;

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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dfz.myapplication.MUtils.SongLoader;
import com.example.dfz.myapplication.MUtils.TimeFormat;
import com.example.dfz.myapplication.Model.Song;
import com.example.dfz.myapplication.Service.MusicService;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements android.support.v7.widget.PopupMenu.OnMenuItemClickListener, LowerBar.LowerBarFragmentTouchListener, LowerBar.LowerBarPlayButtonClickListener, LowerBar.LowerBarNextButtonClickListener {
    private RecyclerView mRecyclerView;
    private MySongAdapter mAdapter;
    private ArrayList<Song> songs = new ArrayList<>();

    private long currentTimeMs = 0;
    private boolean isPlaying = false;

    private String TAG = "MainActivity";

    private MusicService myService;
    boolean mBound = false;

    android.app.FragmentManager fragmentManager;
    android.app.FragmentTransaction fragmentTransaction;


    public static boolean isVisible = true;

    public static final int MSG_NEXT_SONG = 1;
    private static Handler handler;
    public static Messenger messenger;

    private class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (MainActivity.this.isDestroyed()) {
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
    protected void onStart() {
        super.onStart();
        Intent intent2 = new Intent(this, MusicService.class);
        bindService(intent2, mConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "onCreate: bind service");
        handler = new MainHandler();
        messenger = new Messenger(handler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.isVisible = true;
        updateFragment();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = new Intent(this, MusicService.class);

        startService(intent);
        Log.d(TAG, "onCreate: startService");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);


        Log.d(TAG, "onCreate: i am goimg to load songs");

        songs = SongLoader.loadSongs(MainActivity.this);

        Log.d(TAG, "onCreate: song number is " + songs.size());
        mAdapter = new MySongAdapter(MainActivity.this, songs);
        mAdapter.setOnItemClickListener(new MySongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                nowPosition = position;

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
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                }
                fragmentTransaction = fragmentManager.beginTransaction();
                if (fragmentManager.getBackStackEntryCount() > 0)
                    fragmentManager.popBackStack();
                fragmentTransaction.replace(R.id.lower_bar, lowerBar);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                if (mBound) {
                    MainActivity.this.myService.playSong(s);
                } else {
                    Toast.makeText(MainActivity.this, "no service!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void showPopup(View v) {
        android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(this, v);
        //popup.setOptionalIconsVisible(true);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.more_operations_menu, popup.getMenu());
        setIconEnable(popup, true);
        popup.show();
    }

    private void setIconEnable(android.support.v7.widget.PopupMenu menu, boolean enable) {
        try {
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            m.invoke(menu, enable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.play_next:
                return true;
            case R.id.add_to_playlist:
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent destroyIntent = new Intent(this, MusicService.class);
        stopService(destroyIntent);
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
        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
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

    private void updateFragment(){
        Song s = myService.nowPlaySong();
        Toast.makeText(getBaseContext(), "now Song is" + s, Toast.LENGTH_SHORT).show();

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

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
