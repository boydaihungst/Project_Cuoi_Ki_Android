package com.example.dragon.project_cuoi_ki_android.offlineMusic.artist;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.Utils.Utils;
import com.example.dragon.project_cuoi_ki_android.model.Artist;
import com.example.dragon.project_cuoi_ki_android.model.Song;

import java.util.ArrayList;

public class ArtistTabShowAsyncTask extends AsyncTask<Integer,Song,Void> {
    private ArtistTabShowActivity context;
    private ArrayList<Song> listSong = new ArrayList<>();
    public ArtistTabShowAsyncTask(ArtistTabShowActivity context) {
        this.context=context;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        TextView title = (TextView)context.findViewById(R.id.artist_show_title_album);
        title.setText(context.getArtist().getName());
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onProgressUpdate(Song... values) {
        listSong.add(values[0]);
        context.setListSong(listSong);
        context.getArrayAdapter().add(values[0]);
        context.getArrayAdapter().notifyDataSetChanged();
        super.onProgressUpdate(values);
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        listSong = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection="";
        selection = MediaStore.Audio.Media.IS_MUSIC + "!=0 AND " +MediaStore.Audio.Media.ARTIST_ID+" = "+integers[0];
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, MediaStore.Audio.Media.TITLE + " Asc");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    String lyric = Utils.getLyric(url);
                    int typeIndex = url.lastIndexOf(".");
                    String type = typeIndex > 0 ? url.substring(typeIndex + 1) : "";
                    MediaMetadataRetriever metaRetriver;
                    byte[] art = null;
                    Bitmap songImage = null;
                    try {
                        metaRetriver = new MediaMetadataRetriever();
                        metaRetriver.setDataSource(url);
                        art = metaRetriver.getEmbeddedPicture();

                        if (art != null) {
                            songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Song e = new Song();
                    e.setId(Integer.parseInt(id));
                    e.setTitle(title);
                    e.setArtist(artist);
                    e.setUrl(url);
                    e.setLyrics(lyric);
                    e.setDuration(Integer.parseInt(duration));
                    e.setType(type);
                    if (art != null) {
                        e.setPicture(songImage);
                    }
                    publishProgress(e);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return null;
    }
}
