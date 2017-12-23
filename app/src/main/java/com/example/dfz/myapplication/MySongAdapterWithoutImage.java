package com.example.dfz.myapplication;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dfz.myapplication.Model.Song;

import java.util.ArrayList;

/**
 * Created by hp on 2017/12/23.
 */

public class MySongAdapterWithoutImage extends RecyclerView.Adapter<MySongAdapterWithoutImage.ViewHolder> {
    private static final String TAG = "SongAdapterWithoutImage";

    private ArrayList<Song> mDataset;

    private MySongAdapterWithoutImage.OnItemClickListener onItemClickListener;

    private Activity activity;




    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mSongTitleTextView;
        private ImageButton mMoreButton;

        public ViewHolder(CardView v) {
            super(v);
            v.setOnClickListener(v1 -> Log.d(TAG, "Element " + getAdapterPosition() + " clicked."));
            mSongTitleTextView =  v.findViewById(R.id.song_title_in_album);
            mMoreButton =  v.findViewById(R.id.button_more_in_album);
            //mAlbumImageView =  v.findViewById(R.id.albumImage);
            //mMoreButton = (ImageButton)v.findViewById(R.id.buttonMore);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MySongAdapterWithoutImage(Activity activity, ArrayList<Song> myDataset) {
        mDataset = myDataset;
        this.activity = activity;
    }

    // Create new views (invoked by the rank_song_item manager)
    @Override
    public MySongAdapterWithoutImage.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item_without_image, parent, false);


        // set the view's size, margins, paddings and rank_song_item parameters
        return new MySongAdapterWithoutImage.ViewHolder((CardView) v);
    }

    // Replace the contents of a view (invoked by the rank_song_item manager)
    @Override
    public void onBindViewHolder(final MySongAdapterWithoutImage.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mSongTitleTextView.setText("· "+mDataset.get(position).getTitle());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                int pos = holder.getLayoutPosition();

                onItemClickListener.onItemClick(holder.itemView, pos);
            }else {
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


    // Return the size of your dataset (invoked by the rank_song_item manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public interface OnItemClickListener {
        void onItemClick(View itemView, int pos);

        void onItemLongClick(View itemView, int pos);
    }


    public void setOnItemClickListener(MySongAdapterWithoutImage.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

}
