package com.example.dfz.myapplication.Model;

/**
 * Created by DFZ on 2017/12/23.
 */

public class PlayListSong extends Song {

    public final int playlistId;
    public final int idInPlayList;

    public PlayListSong(int id, String title, int trackNumber, int year, long duration, String data,
                        String artist, int artistID, String album, int albumID, final int playlistId, final int idInPlayList) {
        super(id, title, trackNumber, year, duration, data,
                artist, artistID, album, albumID);
        this.playlistId = playlistId;
        this.idInPlayList = idInPlayList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PlayListSong that = (PlayListSong) o;

        if (playlistId != that.playlistId) return false;
        return idInPlayList == that.idInPlayList;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + playlistId;
        result = 31 * result + idInPlayList;
        return result;
    }

    @Override
    public String toString() {
        return super.toString() +
                "PlayListSong{" +
                "playlistId=" + playlistId +
                ", idInPlayList=" + idInPlayList +
                '}';
    }


}
