package com.example.dfz.myapplication.Service;

import android.support.annotation.Nullable;

/**
 * Created by DFZ on 2017/12/10.
 */

public interface PlayBack {
    boolean setDataSource(String path);

    void setNextDataSource(@Nullable String path);

    void setCallbacks(PlaybackCallbacks callbacks);

    boolean isInitialized();

    boolean start();

    void stop();

    void release();

    boolean pause();

    boolean isPlaying();

    int duration();

    int position();

    int seek(int whereto);


    interface PlaybackCallbacks {
        void onTrackWentToNext();

        void onTrackEnded();
    }
}
