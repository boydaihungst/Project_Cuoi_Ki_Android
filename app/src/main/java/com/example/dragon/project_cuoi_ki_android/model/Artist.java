package com.example.dragon.project_cuoi_ki_android.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Artist implements Parcelable {
    private int id;
    private String name;
    private int numOfSong;
    private ArrayList<Song> listSong = new ArrayList<>();

    public Artist() {
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(numOfSong);
        dest.writeTypedList(listSong);
    }

    protected Artist(Parcel in) {
        id = in.readInt();
        name = in.readString();
        numOfSong = in.readInt();
        in.readTypedList(listSong,Song.CREATOR);
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfSong() {
        return numOfSong;
    }

    public void setNumOfSong(int numOfSong) {
        this.numOfSong = numOfSong;
    }

    public ArrayList<Song> getListArtist() {
        return listSong;
    }

    public void setListArtist(ArrayList<Song> listSong) {
        this.listSong = listSong;
    }
}
