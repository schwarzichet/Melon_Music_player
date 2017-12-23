package com.example.dfz.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dfz.myapplication.Model.PlayList;

import java.util.ArrayList;


public class MyPlayListAdapter extends RecyclerView.Adapter<MyPlayListAdapter.ViewHolder> {

    private final ArrayList<PlayList> mPlayLists;
    private MyArtistAdapter.OnItemClickListener mListener;

    MyPlayListAdapter(ArrayList<PlayList> items) {
        mPlayLists = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_playlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.playlistName.setText(mPlayLists.get(position).name);
        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onItemClick(holder.itemView, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlayLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView playlistName;

        ViewHolder(View view) {
            super(view);
            mView = view;
            playlistName =  view.findViewById(R.id.playlist_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + playlistName.getText() + "'";
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int pos);
    }

    public void setOnItemClickListener(MyArtistAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }
}
