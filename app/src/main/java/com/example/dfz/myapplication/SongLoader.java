package com.example.dfz.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.dfz.myapplication.Model.Song;

import java.util.ArrayList;

/**
 * Created by DFZ on 2017/11/18.
 */

public class SongLoader {
    private static final String BASE_SELECTION = MediaStore.Audio.AudioColumns.IS_MUSIC + "=1" + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''";
    private static final String TAG = "songloader";

    public static ArrayList<Song> loadSongs(final Context context) {
        Cursor cursor = makeSongCursor(context, null, null, "TITLE ASC");

        return getSongs(cursor);
    }

    @NonNull
    public static ArrayList<Song> getSongs(@Nullable final Cursor cursor) {
        Log.d(TAG, "getSongs: ");
        ArrayList<Song> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getSongFromCursorImpl(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();
        return songs;
    }

    @NonNull
    public static Song getSong(@Nullable Cursor cursor) {
        Song song;
        if (cursor != null && cursor.moveToFirst()) {
            song = getSongFromCursorImpl(cursor);
        } else {
            song = new Song(-1, "", -1, -1, -1, "", "", -1, "", -1);
        }
        if (cursor != null) {
            cursor.close();
        }
        Log.d(TAG, "getSong: ");
        return song;
    }

    @NonNull
    private static Song getSongFromCursorImpl(@NonNull Cursor cursor) {
        final int id = cursor.getInt(0);
        final String title = cursor.getString(1);
        final int trackNumber = cursor.getInt(2);
        final int year = cursor.getInt(3);
        final long duration = cursor.getLong(4);
        final String data = cursor.getString(5);
        final long dateModified = cursor.getLong(6);
        final int albumID = cursor.getInt(7);
        final String albumName = cursor.getString(8);
        final int artistID = cursor.getInt(9);
        final String artistName = cursor.getString(10);


        return new Song(id, title, trackNumber, year, duration, data, artistName, artistID, albumName, albumID);
    }


    @Nullable
    private static Cursor makeSongCursor(@NonNull final Context context, @Nullable String selection, String[] selectionValues, final String sortOrder) {
        if (selection != null && !selection.trim().equals("")) {
            selection = BASE_SELECTION + " AND " + selection;
        } else {
            selection = BASE_SELECTION;
        }

        try {

            return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            BaseColumns._ID,// 0
                            MediaStore.Audio.Media.TITLE,// 1
                            MediaStore.Audio.Media.TRACK,// 2
                            MediaStore.Audio.Media.YEAR,// 3
                            MediaStore.Audio.Media.DURATION,// 4
                            MediaStore.Audio.Media.DATA,// 5
                            MediaStore.Audio.Media.DATE_MODIFIED,// 6
                            MediaStore.Audio.Media.ALBUM_ID,// 7
                            MediaStore.Audio.Media.ALBUM,// 8
                            MediaStore.Audio.Media.ARTIST_ID,// 9
                            MediaStore.Audio.Media.ARTIST,// 10

                    }, selection, selectionValues, sortOrder);
        } catch (SecurityException e) {
            return null;
        }
    }
}
