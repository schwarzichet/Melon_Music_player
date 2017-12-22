package com.example.dfz.myapplication.Model;

/**
 * Created by DFZ on 2017/12/21.
 */

public class PlayList {
    public final int id;
    public final String name;

    public PlayList(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    public PlayList() {
        this.id = -1;
        this.name = "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayList playlist = (PlayList) o;

        if (id != playlist.id) return false;
        return name != null ? name.equals(playlist.name) : playlist.name == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
