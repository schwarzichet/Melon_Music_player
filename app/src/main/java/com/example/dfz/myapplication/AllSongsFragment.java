package com.example.dfz.myapplication;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dfz.myapplication.MUtils.SongLoader;
import com.example.dfz.myapplication.Model.Song;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class AllSongsFragment extends Fragment {

    private ArrayList<Song> mSongs;


    public AllSongsFragment() {
        // Required empty public constructor
    }

    public static AllSongsFragment newInstance() {
        AllSongsFragment fragment = new AllSongsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_songs, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        mSongs = SongLoader.loadSongs(context);
        MySongAdapter mSongAdapter = new MySongAdapter(getActivity(), mSongs);
        mSongAdapter.setOnItemClickListener(new MySongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int pos) {

                Song s = mSongs.get(pos);

                if(getActivity() instanceof OnItemClick) {
                    Bundle bundle1 = new Bundle();
                    int songId = s.get_Id();
                    bundle1.putInt("songId", songId);
                    ((OnItemClick) getActivity()).playThisSong(bundle1);
                }
            }

            @Override
            public void onItemLongClick(View itemView, int pos) {

            }
        });
        recyclerView.setAdapter(mSongAdapter);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

    public interface OnItemClick {
        void playThisSong(Bundle bundle);
    }
}
