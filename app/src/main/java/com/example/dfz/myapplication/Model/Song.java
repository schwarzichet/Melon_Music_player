package com.example.dfz.myapplication.Model;

import android.content.ContentUris;
import android.net.Uri;


/**
 * Created by DFZ on 2017/10/31.
 */

public class Song {
    final private int _id;

    final private String data;

    final private String title;

    final private String artist;
    final private int artistID;

    final private String album;
    final private int albumID;

    final private int trackNumber;
    final private int year;
    final private long duration;

    public Song(int id, String title, int trackNumber, int year, long duration, String data,
                String artist, int artistID, String album, int albumID) {
        this._id = id;
        this.title = title;
        this.trackNumber = trackNumber;
        this.year = year;
        this.duration = duration;
        this.data = data;
        this.artist = artist;
        this.artistID = artistID;
        this.album = album;
        this.albumID = albumID;
    }

    @Override
    public String toString() {
        return "Song{" +
                "title='" + title + '\'' +
                '}';
    }

    public Uri getAlbumArt() {
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(sArtworkUri, albumID);
    }

    public String getData() {
        return data;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int get_Id() {
        return _id;
    }

    public int getArtistID() {
        return artistID;
    }

    public String getAlbum() {
        return album;
    }

    public int getAlbumID() {
        return albumID;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public int getYear() {
        return year;
    }


}
