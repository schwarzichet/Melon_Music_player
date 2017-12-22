package com.example.dfz.myapplication.MUtils;

import android.content.ContentUris;
import android.net.Uri;

/**
 * Created by DFZ on 2017/12/21.
 */

public class AlbumUtil {
    public static Uri getAlbumArt(int albumID) {
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(sArtworkUri, albumID);
    }

}
