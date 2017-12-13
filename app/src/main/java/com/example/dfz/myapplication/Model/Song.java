package com.example.dfz.myapplication.Model;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;


/**
 * Created by DFZ on 2017/10/31.
 */

public class Song implements Parcelable{
    public static final Song EMPTY_SONG = new Song(-1, "", -1, -1, -1, "", "", -1, "", -1);
    final int _id;

    final String data;

    final String title;

    final String artist;
    final int artistID;

    final String album;
    final int albumID;

    final int trackNumber;
    final int year;
    final long duration;


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

    public long getDuration() {
        return duration;
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

    @Override
    public boolean equals(Object obj) {
        Song s = (Song) obj;
        return this.getData().equals(s.getData());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected Song(Parcel in) {
        this._id = in.readInt();
        this.title = in.readString();
        this.trackNumber = in.readInt();
        this.year = in.readInt();
        this.duration = in.readLong();
        this.data = in.readString();
        this.albumID = in.readInt();
        this.album = in.readString();
        this.artistID = in.readInt();
        this.artist = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this.title);
        dest.writeInt(this.trackNumber);
        dest.writeInt(this.year);
        dest.writeLong(this.duration);
        dest.writeString(this.data);
        dest.writeInt(this.albumID);
        dest.writeString(this.album);
        dest.writeInt(this.artistID);
        dest.writeString(this.artist);
    }
}
