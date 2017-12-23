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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_pager_support);

//        mAdapter = new MyAdapter(getSupportFragmentManager());
//
//        mPager = findViewById(R.id.view_pager);
//        mPager.setAdapter(mAdapter);
//
//        PagerSlidingTabStrip tabs = findViewById(R.id.tabs);
//        tabs.setViewPager(mPager);
    }

//    public class MyAdapter extends FragmentPagerAdapter {
//
//        private final String mTitles[] = {"AllSongs", "Playlists", "Albums", "Artists"};
//
//        public MyAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mTitles[position];
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            Fragment mAlbumsFragment = new AlbumsFragment();
//            return mAlbumsFragment;
//        }
//
//        @Override
//        public int getCount() {
//            return NUM_ITEMS;
//        }
//
//    }

}