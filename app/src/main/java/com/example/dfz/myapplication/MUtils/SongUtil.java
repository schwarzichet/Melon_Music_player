package com.example.dfz.myapplication.MUtils;

import android.content.ContentUris;
import android.net.Uri;

import com.example.dfz.myapplication.Model.Song;

/**
 * Created by hp on 2017/11/29.
 */

public class SongUtil {
    public static Uri getAlbumArt(int albumID) {
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(sArtworkUri, albumID);
    }


}
