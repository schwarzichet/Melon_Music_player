package com.example.dfz.myapplication;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dfz.myapplication.MUtils.SongUtil;
import com.example.dfz.myapplication.Service.MusicService;


/**
 * Created by hp on 2017/11/20.
 */

public class LowerBar extends Fragment {
    private static final String TAG = "LowerBar";
    private String title;
    private String artist;
    private int albumId;
    private String songUri;
    private long duration;




    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        title = getArguments().getString("title");
        artist = getArguments().getString("artist");
        albumId = getArguments().getInt("albumId");
        songUri = getArguments().getString("data");
        duration = getArguments().getLong("duration");

        Intent intent = new Intent(getActivity(), MusicService.class);
        intent.putExtra("songUri", songUri);
        intent.putExtra("duration", duration);

        getActivity().startService(intent);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View lowerbar = inflater.inflate(R.layout.lowerbar_fragment, container, false);
        TextView title_view = (TextView) lowerbar.findViewById(R.id.lowerbar_title);
        TextView artist_view = (TextView) lowerbar.findViewById(R.id.lowerbar_artist);
        ImageView album_view = (ImageView) lowerbar.findViewById(R.id.lowerbar_image);
        Uri imageUri = SongUtil.getAlbumArt(albumId);
        Glide.with(this).load(imageUri).into(album_view);
        artist_view.setText(artist);
        title_view.setText(title);

        final ImageButton playOrPause = lowerbar.findViewById(R.id.lowerbar_playbutton);
//        playOrPause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                player.setPlayWhenReady(!player.getPlayWhenReady());
//
//                if(isPlaying)
//                    playOrPause.setImageResource(R.drawable.ic_pause);
//                else
//                    playOrPause.setImageResource(R.drawable.ic_play_arrow);
//                isPlaying = !isPlaying;
//            }
//
//        });

//        ImageButton nextSong = lowerbar.findViewById(R.id.lowerbar_nextbutton);
//        nextSong.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });







        return lowerbar;
    }

}
