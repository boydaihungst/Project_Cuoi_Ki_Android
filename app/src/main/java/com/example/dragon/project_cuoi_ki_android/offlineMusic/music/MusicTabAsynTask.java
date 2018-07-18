package com.example.dragon.project_cuoi_ki_android.offlineMusic.music;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.model.Song;

import java.util.ArrayList;

public class MusicTabAsynTask extends AsyncTask<Integer,Song,Integer>{
    private MusicTabFragment context;
    private ArrayList<Song> listSong = new ArrayList<>();
    public MusicTabAsynTask(MusicTabFragment context) {
        this.context=context;
    }


    @Override
    protected Integer doInBackground(Integer... integers) {
        listSong = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        Cursor cursor = context.getActivity().getContentResolver().query(uri, null, selection, null, MediaStore.Audio.Media.TITLE + " Asc");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
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
                    Log.d("Song founded", title + " \n" + artist + "\n" + url + "\n" + duration + "\n" + type);
                    Song e = new Song();
                    e.setId(Integer.parseInt(id));
                    e.setTitle(title);
                    e.setArtist(artist);
                    e.setUrl(url);
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

    @Override
    protected void onProgressUpdate(Song... values) {
        listSong.add(values[0]);
        super.onProgressUpdate(values[0]);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        context.setListSong(listSong);
        context.setArrayAdapter(new ListViewMusicAdapter(context.getActivity(), R.layout.list_view_cell_music, context.getListSong()));
        ArrayAdapter arrayAdapter=context.getArrayAdapter();
        arrayAdapter.setNotifyOnChange(true);
        context.getLvMusic().setAdapter(arrayAdapter);
        context.registerForContextMenu(context.getLvMusic());
        context.getLvMusic().setOnItemLongClickListener(context);
        super.onPostExecute(integer);
    }
}
