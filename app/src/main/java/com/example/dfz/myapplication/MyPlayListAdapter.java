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
//    private final OnListFragmentInteractionListener mListener;

    public MyPlayListAdapter(ArrayList<PlayList> items) {
        mPlayLists = items;
//        mListener = listener;
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
}
