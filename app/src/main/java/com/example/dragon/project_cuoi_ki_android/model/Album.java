package com.example.dragon.project_cuoi_ki_android.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Album implements Parcelable {
    private int id;
    private String title;
    private String artist;
    private String picture;
    private int numAlbum;

    public Album() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(picture);
        dest.writeInt(numAlbum);
    }

    protected Album(Parcel in) {
        id = in.readInt();
        title = in.readString();
        artist = in.readString();
        picture = in.readString();
        numAlbum=in.readInt();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getNumAlbum() {
        return numAlbum;
    }

    public void setNumAlbum(int numAlbum) {
        this.numAlbum = numAlbum;
    }

   
}
