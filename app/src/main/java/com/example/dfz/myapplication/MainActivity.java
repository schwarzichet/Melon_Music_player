package com.example.dfz.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.view.LayoutInflater;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.dfz.myapplication.Model.Song;
import com.example.dfz.myapplication.Service.MusicService;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity implements android.support.v7.widget.PopupMenu.OnMenuItemClickListener, LowerBar.LowerBarFragmentTouchListener {
    private RecyclerView mRecyclerView;
    private MySongAdapter mAdapter;
    private ArrayList<Song> songs = new ArrayList<>();
    private int nowPosition = 0;

    private long currentTimeMs = 0;

    private String TAG = "mainactivity";

    private MusicService myService;
    boolean mBound = false;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent2 = new Intent(this, MusicService.class);
        bindService(intent2, mConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "onCreate: bind service");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("songUri", "");
        intent.putExtra("duration", 0);
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
                nowPosition = position;

                Bundle bundle = new Bundle();
                Song s = songs.get(position);
                bundle.putString("title", s.getTitle());
                bundle.putString("artist", s.getArtist());
                bundle.putInt("albumId", s.getAlbumID());
                bundle.putString("songUri", s.getData());
                bundle.putLong("duration", s.getDuration());

                LowerBar lowerBar = new LowerBar();
                lowerBar.setArguments(bundle);
                android.app.FragmentManager fragmentManager = getFragmentManager();
                android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.lower_bar, lowerBar);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


                MainActivity.this.setSong(s.getData());

            }

            @Override
            public void onItemLongClick(View view, int position) {

//                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
//                intent.putExtra("SongUri", songs.get(position).getData());
//                intent.putExtra("albumId", songs.get(position).getAlbumID());
//                intent.putExtra("title", songs.get(position).getTitle());
//                intent.putExtra("artist", songs.get(position).getArtist());
//                intent.putExtra("Duration", songs.get(position).getDuration());
//                startActivity(intent);

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


    public void nextSong(View view) {

        Log.d(TAG, "nextSong: ");
        int position = nowPosition + 1;
        nowPosition++;
        Toast.makeText(MainActivity.this, "long click " + songs.get(position) + " item", Toast.LENGTH_SHORT).show();
        //PlaylistCoverFragment playlistCover = new PlaylistCoverFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", songs.get(position).getTitle());
        bundle.putString("artist", songs.get(position).getArtist());
        bundle.putInt("albumId", songs.get(position).getAlbumID());
        bundle.putString("data", songs.get(position).getData());

        LowerBar lowerBar = new LowerBar();
        lowerBar.setArguments(bundle);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.lower_bar, lowerBar);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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
    public void goToPlayer() {
        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
        intent.putExtra("albumId", songs.get(nowPosition).getAlbumID());
        intent.putExtra("title", songs.get(nowPosition).getTitle());
        intent.putExtra("artist", songs.get(nowPosition).getArtist());
        intent.putExtra("duration", songs.get(nowPosition).getDuration());
        intent.putExtra("currentTimeMs", currentTimeMs);
        startActivity(intent);
    }

    private void setSong(String uri) {
//        Intent intent = new Intent(this, MusicService.class);
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//        Log.d(TAG, "setSong: again set bind");
        myService.setSong(uri);
//        unbindService(mConnection);
    }
}
