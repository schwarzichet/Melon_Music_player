package com.example.dfz.myapplication.MUtils;

import android.content.Context;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.dfz.myapplication.Model.Album;
import com.example.dfz.myapplication.Model.Song;

import java.util.ArrayList;

/**
 * Created by DFZ on 2017/12/13.
 */

public class AlbumLoader {



    @NonNull
    public static ArrayList<Album> getAllAlbums(@NonNull final Context context) {
        ArrayList<Song> songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                null,
                null,
                "TITLE ASC")
        );
        return splitIntoAlbums(songs);
    }

    @NonNull
    public static ArrayList<Album> getAlbums(@NonNull final Context context, String query) {
        ArrayList<Song> songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                MediaStore.Audio.AudioColumns.ALBUM + " LIKE ?",
                new String[]{"%" + query + "%"},
                "TITLE ASC")
        );
        return splitIntoAlbums(songs);
    }

    @NonNull
    public static Album getAlbum(@NonNull final Context context, int albumId) {
        ArrayList<Song> songs = SongLoader.getSongs(SongLoader.makeSongCursor(context, MediaStore.Audio.AudioColumns.ALBUM_ID + "=?", new String[]{String.valueOf(albumId)}, "TITLE ASC"));
        return new Album(songs);
    }

    @NonNull
    public static ArrayList<Album> splitIntoAlbums(@Nullable final ArrayList<Song> songs) {
        ArrayList<Album> albums = new ArrayList<>();
        if (songs != null) {
            for (Song song : songs) {
                getOrCreateAlbum(albums, song.getAlbumID()).songs.add(song);
            }
        }
        return albums;
    }

    private static Album getOrCreateAlbum(ArrayList<Album> albums, int albumId) {
        for (Album album : albums) {
            if (!album.songs.isEmpty() && album.songs.get(0).getAlbumID() == albumId) {
                return album;
            }
        }
        Album album = new Album();
        albums.add(album);
        return album;
    }
}
