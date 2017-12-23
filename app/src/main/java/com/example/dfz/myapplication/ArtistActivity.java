package com.example.dfz.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dfz.myapplication.MUtils.ArtistLoader;
import com.example.dfz.myapplication.MUtils.ArtistUtil;
import com.example.dfz.myapplication.Model.Album;
import com.example.dfz.myapplication.Model.Artist;

public class ArtistActivity extends AppCompatActivity {

    private static final String TAG = "ArtistActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Artist");
        }
        Intent intent = getIntent();
        int artist_id = intent.getIntExtra("artist_id", -1);
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
}
