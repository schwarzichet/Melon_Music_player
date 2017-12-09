package com.example.dfz.myapplication;

import android.content.Intent;
import android.os.Bundle;
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

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity implements android.support.v7.widget.PopupMenu.OnMenuItemClickListener {
    private RecyclerView mRecyclerView;
    private MySongAdapter mAdapter;
    private ArrayList<Song> songs = new ArrayList<>();
    private int nowPosition = 0;

    private String TAG = "mainactivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        //layout manager has been set in content_main
//        // use a linear layout manager
//        mLayoutManager = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(mLayoutManager);

        Log.d(TAG, "onCreate: i am goimg to load songs");

        songs = SongLoader.loadSongs(MainActivity.this);

        Log.d(TAG, "onCreate: song number is "+songs.size());
        mAdapter = new MySongAdapter(MainActivity.this, songs);
        mAdapter.setOnItemClickListener(new MySongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                intent.putExtra("SongUri", songs.get(position).getData());
                intent.putExtra("albumId", songs.get(position).getAlbumID());
                intent.putExtra("title", songs.get(position).getTitle());
                intent.putExtra("artist", songs.get(position).getArtist());
                intent.putExtra("Duration", songs.get(position).getDuration());
                startActivity(intent);

            }

            @Override
            public void onItemLongClick(View view, int position) {
                nowPosition = position;
                Toast.makeText(MainActivity.this, "long click " + songs.get(position) + " item", Toast.LENGTH_SHORT).show();
                //PlaylistCoverFragment playlistCover = new PlaylistCoverFragment();
                Bundle bundle = new Bundle();
                bundle.putString("title", songs.get(position).getTitle());
                bundle.putString("artist", songs.get(position).getArtist());
                bundle.putInt("albumId", songs.get(position).getAlbumID());
                bundle.putString("data", songs.get(position).getData());

                LowerBar lowerBar = new LowerBar();
                lowerBar.setArguments(bundle);
                android.app.FragmentManager fragmentManager=getFragmentManager();
                android.app.FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.lower_bar, lowerBar);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
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


    public void nextSong(View view){

        Log.d(TAG, "nextSong: ");
        int position = nowPosition+1;
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
        android.app.FragmentManager fragmentManager=getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
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

    private void setIconEnable(android.support.v7.widget.PopupMenu menu, boolean enable)
    {
        try
        {
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            m.invoke(menu, enable);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.play_next:;
                return true;
            case R.id.add_to_playlist:;
                return true;
            default:
                return false;
        }
    }

}
