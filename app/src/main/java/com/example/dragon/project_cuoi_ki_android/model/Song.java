package com.example.dragon.project_cuoi_ki_android.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.concurrent.TimeUnit;

public class Song implements Parcelable {
    private int id;
    private String title;
    private String artist;
    private int duration;
    private String type;
    private Bitmap picture;
    private Album album;
    private int numView;
    private String quality;
    private String url;
    private String lyrics;
    public Song() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeInt(duration);
        dest.writeString(type);
        dest.writeParcelable(picture, flags);
        dest.writeInt(numView);
        dest.writeString(quality);
        dest.writeString(url);
        dest.writeInt(id);
        dest.writeString(lyrics);
    }

    protected Song(Parcel in) {
        title = in.readString();
        artist = in.readString();
        duration = in.readInt();
        type = in.readString();
        picture = (Bitmap) in.readParcelable(getClass().getClassLoader());
        numView = in.readInt();
        quality = in.readString();
        url = in.readString();
        id = in.readInt();
        lyrics = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
    public String durationToMinute()  {
        double durationMinute = duration / 60000.0;
        int _durationSecond = ((int) ((durationMinute - (int) durationMinute) * 60));
        String durationSecond = String.valueOf(_durationSecond);
        durationSecond = durationSecond.length() == 1 ? "0" + durationSecond : durationSecond;
        return (int) durationMinute + ":" + durationSecond;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public int getNumView() {
        return numView;
    }

    public void setNumView(int numView) {
        this.numView = numView;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this==obj) return true;
        if (this == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Song s = (Song)obj;
        return s.getId() == this.id;
    }
}
