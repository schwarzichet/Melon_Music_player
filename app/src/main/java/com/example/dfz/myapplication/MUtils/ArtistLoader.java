package com.example.dfz.myapplication.MUtils;

import android.content.Context;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.dfz.myapplication.Model.Album;
import com.example.dfz.myapplication.Model.Artist;
import com.example.dfz.myapplication.Model.Song;

import java.util.ArrayList;

/**
 * Created by DFZ on 2017/12/21.
 */

public class ArtistLoader {


    @NonNull
    public static ArrayList<Artist> getAllArtists(@NonNull final Context context) {
        ArrayList<Song> songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                null,
                null,
                "TITLE ASC")
        );
        return splitIntoArtists(AlbumLoader.splitIntoAlbums(songs));
    }

    @NonNull
    public static ArrayList<Artist> getArtists(@NonNull final Context context, String query) {
        ArrayList<Song> songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                MediaStore.Audio.AudioColumns.ARTIST + " LIKE ?",
                new String[]{"%" + query + "%"},
                "TITLE ASC")
        );
        return splitIntoArtists(AlbumLoader.splitIntoAlbums(songs));
    }

    @NonNull
    public static Artist getArtist(@NonNull final Context context, int artistId) {
        ArrayList<Song> songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                MediaStore.Audio.AudioColumns.ARTIST_ID + "=?",
                new String[]{String.valueOf(artistId)},
                "TITLE ASC")
        );
        return new Artist(AlbumLoader.splitIntoAlbums(songs));
    }

    @NonNull
    public static ArrayList<Artist> splitIntoArtists(@Nullable final ArrayList<Album> albums) {
        ArrayList<Artist> artists = new ArrayList<>();
        if (albums != null) {
            for (Album album : albums) {
                getOrCreateArtist(artists, album.getArtistId()).albums.add(album);
            }
        }
        return artists;
    }

    private static Artist getOrCreateArtist(ArrayList<Artist> artists, int artistId) {
        for (Artist artist : artists) {

            if (!artist.albums.isEmpty() && !artist.albums.get(0).songs.isEmpty() && artist.albums.get(0).songs.get(0).getArtistID() == artistId) {
                return artist;
            }
        }
        Artist album = new Artist();
        artists.add(album);
        return album;
    }
}
