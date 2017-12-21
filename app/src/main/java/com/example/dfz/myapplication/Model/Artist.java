package com.example.dfz.myapplication.Model;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by DFZ on 2017/12/21.
 */

public class Artist {
    public final ArrayList<Album> albums;

    public Artist(ArrayList<Album> albums) {
        this.albums = albums;
    }

    public Artist() {
        this.albums = new ArrayList<>();
    }

    public int getId() {
        return safeGetFirstAlbum().getArtistId();
    }

    public String getName() {
        return safeGetFirstAlbum().getArtistName();
    }

    public int getSongCount() {
        int songCount = 0;
        for (Album album : albums) {
            songCount += album.getSongCount();
        }
        return songCount;
    }

    public int getAlbumCount() {
        return albums.size();
    }

    public ArrayList<Song> getSongs() {
        ArrayList<Song> songs = new ArrayList<>();
        for (Album album : albums) {
            songs.addAll(album.songs);
        }
        return songs;
    }

    @NonNull
    public Album safeGetFirstAlbum() {
        return albums.isEmpty() ? new Album() : albums.get(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        return albums != null ? albums.equals(artist.albums) : artist.albums == null;

    }

    @Override
    public int hashCode() {
        return albums != null ? albums.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "albums=" + albums +
                '}';
    }

}
