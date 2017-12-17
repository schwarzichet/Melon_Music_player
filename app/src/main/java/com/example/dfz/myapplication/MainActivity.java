package com.example.dfz.myapplication;

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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dfz.myapplication.MUtils.SongLoader;
import com.example.dfz.myapplication.MUtils.SongUtil;
import com.example.dfz.myapplication.MUtils.TimeFormat;
import com.example.dfz.myapplication.Model.Song;
import com.example.dfz.myapplication.Service.MusicService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL;

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


    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

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
        if (mBound) {
            Log.d(TAG, "onResume: yes bound");
            updateFragment();
        }else {
            Log.d(TAG, "onResume: oh, not bound?");

//            Intent intent2 = new Intent(this, MusicService.class);
//            bindService(intent2, mConnection, Context.BIND_AUTO_CREATE);
//            Log.d(TAG, "onCreate: bind service");
//            handler = new MainHandler();
//            messenger = new Messenger(handler);

//            updateFragment();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        setSupportActionBar(toolbar);

        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item_text, mPlanetTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//                ImageView drawerImage = findViewById(R.id.drawer_imageView);
//                Glide.with(getBaseContext()).load(imageUri).into(drawerImage);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

                Song s = myService.nowPlaySong();
                ImageView drawerImage = findViewById(R.id.drawer_imageView);
                Uri imageUri = SongUtil.getAlbumArt(s.getAlbumID());
                Glide.with(getBaseContext()).load(imageUri).into(drawerImage);

            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);



        Intent intent = new Intent(this, MusicService.class);

        startService(intent);
        Log.d(TAG, "onCreate: startService");





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
                Intent intent = new Intent(MainActivity.this, FragmentPagerSupport.class);
                startActivity(intent);
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

    private void updateFragment() {
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

    private class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        switch (position){
            case 0:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case 1:
                Intent intent = new Intent(MainActivity.this, LastFMLoginActivity.class);
                startActivity(intent);
            default:
                break;
        }
    }

    private void updateDrawer(){


    }
}
