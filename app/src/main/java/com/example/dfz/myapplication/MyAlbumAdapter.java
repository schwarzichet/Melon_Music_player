package com.example.dfz.myapplication;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dfz.myapplication.Model.Album;

import java.util.ArrayList;


public class MyAlbumAdapter extends RecyclerView.Adapter<MyAlbumAdapter.ViewHolder> {
    private static final String TAG = "MyAlbumAdapter";

    public final ArrayList<Album> mAlbums;
    //private final OnListFragmentInteractionListener mListener;
    private Activity activity;

    private MyAlbumAdapter.OnItemClickListener onItemClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mAlbumArt;
        private TextView mAlbumName;

        public ViewHolder(CardView v) {
            super(v);
            v.setOnClickListener(v1 -> Log.d(TAG, "Element " + getAdapterPosition() + " clicked."));
            mAlbumArt = v.findViewById(R.id.album_art);
            mAlbumName = v.findViewById(R.id.album_name);
        }
    }

//    public MyAlbumAdapter(ArrayList<Album> items, OnListFragmentInteractionListener listener) {
//        mAlbums = items;
//        mListener = listener;
//    }

    public MyAlbumAdapter(Activity activity, ArrayList<Album> mAlbums) {
        this.mAlbums = mAlbums;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_album_item, parent, false);
        return new ViewHolder((CardView) view);
    }

    @Override
    public void onBindViewHolder(MyAlbumAdapter.ViewHolder holder, int position) {
        holder.mAlbumName.setText(mAlbums.get(position).getTitle());
        Uri imageUri = mAlbums.get(position).safeGetFirstSong().getAlbumArt();
        Glide.with(activity).load(imageUri).into(holder.mAlbumArt);

        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                int pos = holder.getLayoutPosition();
                onItemClickListener.onItemClick(holder.itemView, pos);
            }
        });
    }
//
//    @Override
//    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
//        holder.artistStat.setText(mValues.get(position).content);
//
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
//            }
//        });
//    }
//
    @Override
    public int getItemCount() {
        return mAlbums.size();
    }


    public interface OnItemClickListener {
        void onItemClick(View itemView, int pos);
    }

    public void setOnItemClickListener(MyAlbumAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


}
