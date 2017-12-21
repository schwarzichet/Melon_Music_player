package com.example.dfz.myapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.vpaliy.last_fm_api.model.Album;

import java.util.ArrayList;
import java.util.List;

public class FragmentPagerSupport extends FragmentActivity {

    static final int NUM_ITEMS = 4;
    MyAdapter mAdapter;
    ViewPager mPager;
    //ArrayList<String> mTitles;
    private static int nowFragmentIndex = 0;
//    android.app.FragmentManager fragmentManager;
//    android.app.FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_pager_support);

        mAdapter = new MyAdapter(getSupportFragmentManager());

        mPager = findViewById(R.id.view_pager);
        mPager.setAdapter(mAdapter);

        PagerSlidingTabStrip tabs = findViewById(R.id.tabs);
        tabs.setViewPager(mPager);
    }

    public class MyAdapter extends FragmentPagerAdapter {

        private final String mTitles[] = {"Songs", "Playlists", "Albums", "Artists"};

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            Fragment mAlbumsFragment = new AlbumsFragment();
            return mAlbumsFragment;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

//        @Override
//        public ListFragment getItem(int position) {
//            return com.example.dfz.myapplication.ArrayListFragment.newInstance(position);
//        }
    }

//    public static class com.example.dfz.myapplication.ArrayListFragment extends ListFragment {
//        int mNum;
//        static com.example.dfz.myapplication.AlbumsFragment mAlbumsFragment;
//
//        /**
//         * Create a new instance of CountingFragment, providing "num"
//         * as an argument.
//         */
//        static com.example.dfz.myapplication.ArrayListFragment newInstance(int num) {
//            switch (num) {
//                case 0:
//                    mAlbumsFragment = new com.example.dfz.myapplication.AlbumsFragment();
//                case 1:
//            }
//            com.example.dfz.myapplication.ArrayListFragment f = new com.example.dfz.myapplication.ArrayListFragment();
//
//            // Supply num input as an argument.
//            Bundle args = new Bundle();
//            args.putInt("num", num);
//            f.setArguments(args);
//
//            return f;
//        }
//
//        /**
//         * When creating, retrieve this instance's number from its arguments.
//         */
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
//        }
//
//        /**
//         * The Fragment's UI is just a simple text view showing its
//         * instance number.
//         */
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View v = inflater.inflate(R.layout.activity_fragment_pager_support, container, false);
//            return v;
//        }
//
//        @Override
//        public void onActivityCreated(Bundle savedInstanceState) {
//            super.onActivityCreated(savedInstanceState);
//            switch (nowFragmentIndex) {
//                case 0: setListAdapter(new MyAlbumAdapter(this, mAlbums));
//            }
//
//            setListAdapter(new ArrayAdapter<String>(getActivity(),
//                    android.R.layout.));
//        }
//
//        @Override
//        public void onListItemClick(ListView l, View v, int position, long id) {
//            Log.i("FragmentList", "Item clicked: " + id);
//        }
//    }
}