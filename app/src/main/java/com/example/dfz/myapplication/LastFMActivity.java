package com.example.dfz.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dfz.myapplication.MUtils.LastFMUtil;
import com.vpaliy.last_fm_api.LastFm;
import com.vpaliy.last_fm_api.LastFmService;
import com.vpaliy.last_fm_api.model.Session;
import com.vpaliy.last_fm_api.model.Track;
import com.vpaliy.last_fm_api.model.TrackPage;
import com.vpaliy.last_fm_api.model.User;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LastFMActivity extends AppCompatActivity {

    private static final String TAG = "LastFMActivity";

    private SharedPreferences preference;
    private String username;
    private Session session;

    private RecyclerView mRecyclerView;
    private MyRecentAdapter mAdapter;
    private ArrayList<SimpleSong> mSimpleSongs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_fm);
        preference = getSharedPreferences(LastFMLoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        username = preference.getString("username", "unKnown");
        session = Session.convertFromString(preference.getString("key", null));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Last.fm");
        }
        ImageView userImage = findViewById(R.id.userImage);
        TextView nicknameView = findViewById(R.id.nickname);
        TextView usernameView = findViewById(R.id.username);

        TextView playCount = findViewById(R.id.playcount);
        TextView playListCount = findViewById(R.id.playlist);


        LastFmService service = LastFm.create(LastFMUtil.API_KEY)
                .createService(this);


        service.fetchUserInfo(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    User user = response.result;
                    Log.d(TAG, "onCreate: image num" + user.image.size());
                    String nickName = user.name;
                    String realName = user.realname;
                    if (realName == null) {
                        realName = nickName;
                        if (realName == null) {
                            nickName = "null";
                            realName = "null";
                        }
                    }
                    Glide.with(this).load(user.image.get(3).text).into(userImage);
                    Log.d(TAG, "onCreate: nickname" + user.name);
                    Log.d(TAG, "onCreate: realname" + user.realname);
                    Log.d(TAG, "onCreate: playcount" + user.playcount);
                    Log.d(TAG, "onCreate: playlist" + user.playlists);
                    nicknameView.setText(nickName);
                    usernameView.setText(realName);
                    playCount.setText(String.valueOf(user.playcount));
                    playListCount.setText(String.valueOf(user.playlists));

                });


        mRecyclerView = findViewById(R.id.recent_list);
        mRecyclerView.setHasFixedSize(true);


        Log.d(TAG, "onCreate: i am fetch songs ranks");
        mSimpleSongs = new ArrayList<>();
        service.fetchUserRecentTracks(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    TrackPage trackPage = response.result;
                    Log.d(TAG, "onCreate: "+trackPage );

                    for (Track t : trackPage.track) {
                        mSimpleSongs.add(new SimpleSong(t.name, t.artist.name, t.album.name, t.playcount));
                    }
                    mAdapter = new MyRecentAdapter(mSimpleSongs);
                    mRecyclerView.setAdapter(mAdapter);
                });

    }

    public void toRankActivity(View view) {
        Intent intent = new Intent(LastFMActivity.this, RankActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(LastFMActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private class MyRecentAdapter extends RecyclerView.Adapter<MyRecentAdapter.ViewHolder> {
        private ArrayList<SimpleSong> mSimpleSongs;

        MyRecentAdapter(ArrayList<SimpleSong> mSimpleSongs) {
            this.mSimpleSongs = mSimpleSongs;
            Log.d(TAG, "MyRecentAdapter: " + mSimpleSongs.size());

        }

        @Override
        public MyRecentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recent_song_item, parent, false);
            return new MyRecentAdapter.ViewHolder((CardView) v);
        }

        @Override
        public void onBindViewHolder(MyRecentAdapter.ViewHolder holder, int position) {
            holder.index.setText(String.valueOf(position + 1));
            holder.indexSongTitle.setText(mSimpleSongs.get(position).title);
            holder.indexArtist.setText(mSimpleSongs.get(position).artist);
        }

        @Override
        public int getItemCount() {
            return mSimpleSongs.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView index;
            private TextView indexSongTitle;
            private TextView indexArtist;

            ViewHolder(CardView itemView) {
                super(itemView);
                itemView.setOnClickListener(v -> Log.d(TAG, "Element " + getAdapterPosition() + " clicked."));
                index = itemView.findViewById(R.id.song_index);
                indexSongTitle = itemView.findViewById(R.id.recent_song_title);
                indexArtist = itemView.findViewById(R.id.recent_song_artist);
            }
        }


    }

    private class SimpleSong {
        String title;
        String artist;
        String album;
        long times;

        SimpleSong(String title, String artist, String album, long times) {
            this.title = title;
            this.artist = artist;
            this.album = album;
            this.times = times;
        }
    }
}
