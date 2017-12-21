package com.example.dfz.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.dfz.myapplication.MUtils.AlbumLoader;
import com.example.dfz.myapplication.Model.Album;

import java.util.ArrayList;

/**
 * Created by hp on 2017/12/13.
 */

public class ArrayListFragment extends ListFragment {
    int mNum;
    ArrayList<Album> mAlbums;
//
//    Fragment(int position, ArrayList<Album> mAlbums) {
//        this.mNum = position;
//        this.mAlbums = mAlbums;
//    }

    /**
     * Create a new instance of CountingFragment, providing "num"
     * as an argument.
     */
    static Fragment newInstance(int num) {
//        switch (num) {
//            case 0:
//                Fragment mAlbumsFragment = new AlbumsFragment();
//                return mAlbumsFragment;
//            case 1:
//        }
        Fragment mAlbumsFragment = new AlbumsFragment();
        return mAlbumsFragment;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_fragment_pager_support, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        switch (mNum) {
            case 0:
                ArrayList<Album> mAlbums = AlbumLoader.getAllAlbums(getContext());
                //setAdapter(new MyAlbumAdapter(getActivity(), mAlbums);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);
    }
}
