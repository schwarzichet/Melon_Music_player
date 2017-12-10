package com.example.dfz.myapplication;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    private long currentMs;

    private GestureDetectorCompat mDetector;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        title = getArguments().getString("title");
        artist = getArguments().getString("artist");
        albumId = getArguments().getInt("albumId");
        songUri = getArguments().getString("songUri");
        duration = getArguments().getLong("duration");

//        Intent intent = new Intent(getActivity(), MusicService.class);
//        intent.putExtra("songUri", songUri);
//        intent.putExtra("duration", duration);
//        getActivity().startService(intent);




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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mDetector = new GestureDetectorCompat(getContext(), new MyGestureListener());

        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (getActivity() instanceof LowerBarFragmentTouchListener){
                    return mDetector.onTouchEvent(motionEvent);
                }
                return false;
            }
        });
    }


    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG,"onDown: " + event.toString());
            if (getActivity() instanceof LowerBarFragmentTouchListener){
                ((LowerBarFragmentTouchListener) getActivity()).goToPlayer();
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + e1.toString() + e2.toString());
            if(velocityY > 0)
            {
                if (getActivity() instanceof LowerBarFragmentTouchListener){
                    ((LowerBarFragmentTouchListener) getActivity()).goToPlayer();
                }
            }
            return true;
        }
    }

    public interface LowerBarFragmentTouchListener{
        void goToPlayer();
    }

}
