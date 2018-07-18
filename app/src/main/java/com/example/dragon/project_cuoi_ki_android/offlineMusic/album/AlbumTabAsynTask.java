package com.example.dragon.project_cuoi_ki_android.offlineMusic.album;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.model.Album;

import java.util.ArrayList;

public class AlbumTabAsynTask extends AsyncTask<Integer,Album,Integer> {
    private AlbumTabFragment context;
    private ArrayList<Album> listAlbum = new ArrayList<>();
    public AlbumTabAsynTask(AlbumTabFragment context) {
        this.context=context;
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        listAlbum = new ArrayList<>();
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] { MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ARTIST
                , MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums.NUMBER_OF_SONGS };
        Cursor cursor = context.getActivity().getContentResolver().query(uri, projection, null, null, MediaStore.Audio.Albums.ALBUM +" Asc");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
                    String numOfSong = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
                    String picture = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                    Album a = new Album();
                    a.setId(Integer.parseInt(id));
                    a.setTitle(title);
                    a.setArtist(artist);
                    a.setNumAlbum(Integer.parseInt(numOfSong));
                    a.setPicture(picture);
                    Log.d("founded album",id+"="+title+"="+artist+"="+numOfSong+"="+picture);
                    publishProgress(a);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        int numberOfColumns = 2;
        context.getRvAlbum().setLayoutManager(new GridLayoutManager(context.getActivity(), numberOfColumns));
        context.setListAlbum(listAlbum);
        context.setAdapter(new RecycleViewCustomAdapter(context.getActivity(), R.layout.recycler_view_cell_album, context.getListAlbum()));
        RecycleViewCustomAdapter adapter= context.getAdapter();
        context.getRvAlbum().setAdapter(adapter);
        adapter.setClickListener(context);
        super.onPostExecute(integer);
    }

    @Override
    protected void onProgressUpdate(Album... values) {
        listAlbum.add(values[0]);
        super.onProgressUpdate(values);
    }
}
