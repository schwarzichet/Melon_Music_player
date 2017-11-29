package com.example.dfz.myapplication;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dfz.myapplication.MUtils.SongUtil;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


/**
 * Created by hp on 2017/11/20.
 */

public class LowerBar extends Fragment {
    private static final String TAG = "LowerBar";
    private String title;
    private String artist;
    private int albumId;
    private SimpleExoPlayer player;
    private String songUri;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;

    //private MySongAdapter()
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        title = getArguments().getString("title");
        artist = getArguments().getString("artist");
        albumId = getArguments().getInt("albumId");
        songUri = getArguments().getString("data");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 拉伸该 Fragment 的布局

        View lowerbar = inflater.inflate(R.layout.lowerbar_fragment, container, false);
        TextView title_view = (TextView) lowerbar.findViewById(R.id.lowerbar_title);
        TextView artist_view = (TextView) lowerbar.findViewById(R.id.lowerbar_artist);
        ImageView album_view = (ImageView) lowerbar.findViewById(R.id.lowerbar_image);
        Uri imageUri = SongUtil.getAlbumArt(albumId);
        Glide.with(this).load(imageUri).into(album_view);
        artist_view.setText(artist);
        title_view.setText(title);

        return lowerbar;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getActivity()),
                new DefaultTrackSelector(), new DefaultLoadControl());


        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);

        Uri uri = Uri.parse(songUri);
        Log.d(TAG, "initializePlayer: uri = " + uri);
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);
//        playerView.setUseArtwork(true);
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }


    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new FileDataSourceFactory(),
                new DefaultExtractorsFactory(), null, null);
    }

}
