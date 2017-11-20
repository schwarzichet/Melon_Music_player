package com.example.dfz.myapplication;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hp on 2017/11/20.
 */

public class LowerBar extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 拉伸该 Fragment 的布局
        return inflater.inflate(R.layout.lowerbar_layout, container, false);
    }
}
