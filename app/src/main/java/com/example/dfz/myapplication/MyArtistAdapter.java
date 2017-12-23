package com.example.dfz.myapplication;

import android.app.Activity;
import android.support.v4.app.Fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dfz.myapplication.MUtils.ArtistUtil;
import com.example.dfz.myapplication.Model.Artist;

import java.util.ArrayList;


public class MyArtistAdapter extends RecyclerView.Adapter<MyArtistAdapter.ViewHolder> {

    private final ArrayList<Artist> artists;
    private Activity activity;
    private Fragment fragment;
//    private final OnListFragmentInteractionListener mListener;
    private MyArtistAdapter.OnItemClickListener onItemClickListener;

    public MyArtistAdapter(ArrayList<Artist> items, Activity activity, Fragment fragment) {
        artists = items;


        this.activity = activity;
        this.fragment = fragment;
//        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_artist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Artist artist = artists.get(position);
        holder.artistName.setText(artists.get(position).getName());
        holder.artistStat.setText(String.valueOf(artist.getAlbumCount()) + "albums-" + artist.getSongCount() + "songs ");
        ArtistUtil.setArtistImage(activity, artists.get(position).getName(), artists.get(position).getId(),holder.artistImage, 2);

        holder.itemView.setOnClickListener(v -> {
            if (null != onItemClickListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                onItemClickListener.onItemClick(holder.itemView, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        final View mView;
        final TextView artistName;
        final TextView artistStat;
        final ImageView artistImage;

        ViewHolder(View view) {
            super(view);
//            mView = view;
            artistName = view.findViewById(R.id.artist_name_artist_activity);
            artistStat =  view.findViewById(R.id.artist_stat);
            artistImage =  view.findViewById(R.id.artist_image_artist_activity);
            setIsRecyclable(false);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + artistStat.getText() + "'";
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
