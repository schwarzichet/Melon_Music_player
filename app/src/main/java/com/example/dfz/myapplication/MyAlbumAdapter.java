package com.example.dfz.myapplication;

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
import com.example.dfz.myapplication.AlbumItemFragment.OnListFragmentInteractionListener;
import com.example.dfz.myapplication.Model.Album;
import com.example.dfz.myapplication.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyAlbumAdapter extends RecyclerView.Adapter<MyAlbumAdapter.ViewHolder> {
    private static final String TAG = "MyAlbumAdapter";

    public final ArrayList<Album> mAlbums;
    private final OnListFragmentInteractionListener mListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mAlbumArt;
        private TextView mAlbumName;

        public ViewHolder(CardView v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            mAlbumArt = v.findViewById(R.id.album_art);
            mAlbumName = v.findViewById(R.id.album_name);
        }
    }

    public MyAlbumAdapter(ArrayList<Album> items, OnListFragmentInteractionListener listener) {
        mAlbums = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_album_item, parent, false);
        return new ViewHolder((CardView) view);
    }

    @Override
    public void onBindViewHolder(MyAlbumAdapter.ViewHolder holder, int position) {
//        holder.mAlbumName.setText(mAlbums.get(position).getTitle());
//        holder.mAlbumArt.setImageResource(mAlbums.get(position).get);
//
//
//        Uri imageUri = mDataset.get(position).getAlbumArt();
//        Log.d(TAG, "onBindViewHolder: " + imageUri);
//        Glide.with(activity).load(imageUri).into(holder.mAlbumImageView);
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                if (onItemClickListener != null) {
//                    int pos = holder.getLayoutPosition();
//                    onItemClickListener.onItemClick(holder.itemView, pos);
//                }
//            }
//        });
//
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (onItemClickListener != null) {
//                    int pos = holder.getLayoutPosition();
//                    onItemClickListener.onItemLongClick(holder.itemView, pos);
//                }
//                return true;
//            }
//        });
//
    }
//
//    @Override
//    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
//        holder.mContentView.setText(mValues.get(position).content);
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
//    @Override
    public int getItemCount() {
        return mAlbums.size();
    }

}
