package com.example.dfz.myapplication;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dfz.myapplication.MUtils.SongUtil;
import com.google.android.exoplayer2.SimpleExoPlayer;

/**
 * Created by hp on 2017/11/20.
 */

public class LowerBar extends Fragment {

    private String title;
    private String artist;
    private int albumId;
    private SimpleExoPlayer mPlayer;

    //private MySongAdapter()
    @Override
    public void onCreate(Bundle saveInstanceState)
    {
        super.onCreate(saveInstanceState);
        title=getArguments().getString("title");
        artist=getArguments().getString("artist");
        albumId=getArguments().getInt("albumId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 拉伸该 Fragment 的布局

        View lowerbar = inflater.inflate(R.layout.lowerbar_fragment, container, false);
        TextView title_view = (TextView)lowerbar.findViewById(R.id.lowerbar_title);
        TextView artist_view = (TextView)lowerbar.findViewById(R.id.lowerbar_artist);
        ImageView album_view = (ImageView)lowerbar.findViewById(R.id.lowerbar_image);
        Uri imageUri = SongUtil.getAlbumArt(albumId);
        Glide.with(this).load(imageUri).into(album_view);
        artist_view.setText(artist);
        title_view.setText(title);

        return lowerbar;
    }

}
