package com.example.dfz.myapplication;

import android.content.Context;
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

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RankActivity extends AppCompatActivity {
    private final String TAG = "RankActivity";
    private RecyclerView mRecyclerView;
    private SharedPreferences preference;
    private String username;
    private Session session;

    private MyRankAdapter mAdapter;
    private List<Track> mRankTracks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        preference = getSharedPreferences(LastFMLoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        username = preference.getString("username", "unKnown");
        session = Session.convertFromString(preference.getString("key", null));

        mRecyclerView = findViewById(R.id.rank_list);
        mRecyclerView.setHasFixedSize(true);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Top tracks");
        }

        LastFmService service = LastFm.create(LastFMUtil.API_KEY)
                .createService(this);

        service.fetchUserTopTracks(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    TrackPage trackPage = response.result;
                    mRankTracks = trackPage.track;
                    Log.d(TAG, "onCreate: mRankTracks 0:"+printTrack(mRankTracks.get(0)));

                    mAdapter = new MyRankAdapter(mRankTracks);
                    mRecyclerView.setAdapter(mAdapter);
                });
    }

    private class MyRankAdapter extends RecyclerView.Adapter<MyRankAdapter.ViewHolder>{
        private final List<Track> mRankTracks;

        MyRankAdapter(List<Track> mRankTracks) {
            this.mRankTracks = mRankTracks;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rank_song_item, parent, false);

            return new MyRankAdapter.ViewHolder((CardView) v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.rank.setText(String.valueOf(position + 1));
            Track t = mRankTracks.get(position);
            holder.rankSongTitle.setText(t.name);
            holder.rankArtist.setText(t.artist.name);
            holder.playCount.setText(String.valueOf(t.playcount)+" times");
            Glide.with(getBaseContext()).load(t.image.get(1).text).into(holder.rankImage);
        }

        @Override
        public int getItemCount() {
            return mRankTracks.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView rank;
            private TextView rankSongTitle;
            private TextView rankArtist;
            private TextView playCount;
            private ImageView rankImage;
            ViewHolder(CardView itemView) {
                super(itemView);
                itemView.setOnClickListener(v -> Log.d(TAG, "Element " + getAdapterPosition() + " clicked."));
                rank = itemView.findViewById(R.id.song_rank);
                rankSongTitle = itemView.findViewById(R.id.rank_song_title);
                rankArtist = itemView.findViewById(R.id.rank_song_artist);
                playCount = itemView.findViewById(R.id.play_count);
                rankImage = itemView.findViewById(R.id.rank_image);
            }
        }
    }

    private String printTrack(Track t){
        return "album:" + t.album +
                "playCount: " + t.playcount +
                "artist: " + t.artist +
                "name:" + t.name +
                "image:" + t.image;
    }
}
