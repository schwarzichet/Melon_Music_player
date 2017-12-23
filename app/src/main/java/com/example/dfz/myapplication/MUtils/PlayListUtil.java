package com.example.dfz.myapplication.MUtils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.example.dfz.myapplication.Model.PlayListSong;

import java.util.ArrayList;

/**
 * Created by DFZ on 2017/12/23.
 */

public class PlayListUtil {
    @NonNull
    public static ArrayList<PlayListSong> getPlayListSongList(@NonNull final Context context, final int playlistId) {
        ArrayList<PlayListSong> songs = new ArrayList<>();
        Cursor cursor = makePlayListSongCursor(context, playlistId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getPlayListSongFromCursorImpl(cursor, playlistId));
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return songs;
    }

    @NonNull
    private static PlayListSong getPlayListSongFromCursorImpl(@NonNull Cursor cursor, int playlistId) {
        final int id = cursor.getInt(0);
        final String title = cursor.getString(1);
        final int trackNumber = cursor.getInt(2);
        final int year = cursor.getInt(3);
        final long duration = cursor.getLong(4);
        final String data = cursor.getString(5);
        final int dateModified = cursor.getInt(6);
        final int albumId = cursor.getInt(7);
        final String albumName = cursor.getString(8);
        final int artistId = cursor.getInt(9);
        final String artistName = cursor.getString(10);
        final int idInPlaylist = cursor.getInt(11);

        return new PlayListSong(id, title, trackNumber, year, duration, data, artistName, artistId, albumName, albumId, playlistId, idInPlaylist);
    }

    public static Cursor makePlayListSongCursor(@NonNull final Context context, final int playlistId) {
        try {
            return context.getContentResolver().query(
                    MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                    new String[]{
                            MediaStore.Audio.Playlists.Members.AUDIO_ID,// 0
                            MediaStore.Audio.AudioColumns.TITLE,// 1
                            MediaStore.Audio.AudioColumns.TRACK,// 2
                            MediaStore.Audio.AudioColumns.YEAR,// 3
                            MediaStore.Audio.AudioColumns.DURATION,// 4
                            MediaStore.Audio.AudioColumns.DATA,// 5
                            MediaStore.Audio.AudioColumns.DATE_MODIFIED,// 6
                            MediaStore.Audio.AudioColumns.ALBUM_ID,// 7
                            MediaStore.Audio.AudioColumns.ALBUM,// 8
                            MediaStore.Audio.AudioColumns.ARTIST_ID,// 9
                            MediaStore.Audio.AudioColumns.ARTIST,// 10
                            MediaStore.Audio.Playlists.Members._ID // 11
                    }, SongLoader.BASE_SELECTION, null,
                    MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);
        } catch (SecurityException e) {
            return null;
        }
    }
}
