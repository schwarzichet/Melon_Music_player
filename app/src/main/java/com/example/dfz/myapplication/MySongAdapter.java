package com.example.dfz.myapplication;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dfz.myapplication.Model.Song;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by DFZ on 2017/10/31.
 */

public class MySongAdapter extends RecyclerView.Adapter<MySongAdapter.ViewHolder> {
    private static final String TAG = "MySongAdapter";

    private ArrayList<Song> mDataset;

    private MySongAdapter.OnItemClickListener onItemClickListener;

    private Activity activity;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mSongTitleTextView;
        private TextView mArtistTextView;
        private ImageView mAlbumImageView;
        //private ImageButton mMoreButton;

        public ViewHolder(CardView v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            mSongTitleTextView = v.findViewById(R.id.SongTitle);
            mArtistTextView = v.findViewById(R.id.ArtistName);
            mAlbumImageView = v.findViewById(R.id.albumImage);
            //mMoreButton = (ImageButton)v.findViewById(R.id.buttonMore);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MySongAdapter(Activity activity, ArrayList<Song> myDataset) {
        mDataset = myDataset;
        this.activity = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MySongAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item_view, parent, false);

        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder((CardView) v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mSongTitleTextView.setText(mDataset.get(position).getTitle());
        holder.mArtistTextView.setText(mDataset.get(position).getArtist());

        Uri imageUri = mDataset.get(position).getAlbumArt();
        Log.d(TAG, "onBindViewHolder: " + imageUri);
        Glide.with(activity).load(imageUri).into(holder.mAlbumImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();

                    onItemClickListener.onItemClick(holder.itemView, pos);
                }else {
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemLongClick(holder.itemView, pos);
                }
                return true;
            }
        });

//        holder.mMoreButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public interface OnItemClickListener {
        void onItemClick(View itemView, int pos);

        void onItemLongClick(View itemView, int pos);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

}
