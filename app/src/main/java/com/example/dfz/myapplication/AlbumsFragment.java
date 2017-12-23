package com.example.dfz.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dfz.myapplication.MUtils.AlbumLoader;
import com.example.dfz.myapplication.Model.Album;

import java.util.ArrayList;

/**
 * Created by hp on 2017/12/17.
 */

public class AlbumsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 3;

    private ArrayList<Album> mAlbums;

    private OnFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AlbumsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AlbumsFragment newInstance(int columnCount) {
        AlbumsFragment fragment = new AlbumsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_item_list, container, false);

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));

        mAlbums = AlbumLoader.getAllAlbums(getContext());
        MyAlbumAdapter mAlbumAdapter = new MyAlbumAdapter(getActivity(), mAlbums);
        mAlbumAdapter.setOnItemClickListener((itemView, pos) -> {

            Album album = mAlbums.get(pos);

            Log.d("go to", "success!");
            Intent intent = new Intent(getActivity(), AlbumActivity.class);
            intent.putExtra("albumId", album.getId());
            Log.d("go to album activity", "success!");
            startActivity(intent);
        });
        recyclerView.setAdapter(mAlbumAdapter);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Album item);
    }
}
