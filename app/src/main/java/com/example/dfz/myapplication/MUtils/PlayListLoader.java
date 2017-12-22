package com.example.dfz.myapplication.MUtils;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.dfz.myapplication.Model.PlayList;

import java.util.ArrayList;

/**
 * Created by DFZ on 2017/12/21.
 */

public class PlayListLoader {
    @NonNull
    public static ArrayList<PlayList> getAllPlayLists(@NonNull final Context context) {
        return getAllPlayLists(makePlayListCursor(context, null, null));
    }

    @NonNull
    public static PlayList getPlayList(@NonNull final Context context, final int PlayListId) {
        return getPlayList(makePlayListCursor(
                context,
                BaseColumns._ID + "=?",
                new String[]{
                        String.valueOf(PlayListId)
                }
        ));
    }

    @NonNull
    public static PlayList getPlayList(@NonNull final Context context, final String PlayListName) {
        return getPlayList(makePlayListCursor(
                context,
                MediaStore.Audio.PlaylistsColumns.NAME + "=?",
                new String[]{
                        PlayListName
                }
        ));
    }

    @NonNull
    public static PlayList getPlayList(@Nullable final Cursor cursor) {
        PlayList PlayList = new PlayList();

        if (cursor != null && cursor.moveToFirst()) {
            PlayList = getPlayListFromCursorImpl(cursor);
        }
        if (cursor != null)
            cursor.close();
        return PlayList;
    }

    @NonNull
    public static ArrayList<PlayList> getAllPlayLists(@Nullable final Cursor cursor) {
        ArrayList<PlayList> PlayLists = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                PlayLists.add(getPlayListFromCursorImpl(cursor));
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return PlayLists;
    }

    @NonNull
    private static PlayList getPlayListFromCursorImpl(@NonNull final Cursor cursor) {
        final int id = cursor.getInt(0);
        final String name = cursor.getString(1);
        return new PlayList(id, name);
    }

    @Nullable
    public static Cursor makePlayListCursor(@NonNull final Context context, final String selection, final String[] values) {
        try {
            return context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    new String[]{
                        /* 0 */
                            BaseColumns._ID,
                        /* 1 */
                            MediaStore.Audio.PlaylistsColumns.NAME
                    }, selection, values, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);
        } catch (SecurityException e) {
            return null;
        }
    }
}
