package com.example.dragon.project_cuoi_ki_android.Controller;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.example.dragon.project_cuoi_ki_android.MainActivity;
import com.example.dragon.project_cuoi_ki_android.model.Song;

public class FindSongByIDAsycTask extends AsyncTask<Void, Song, Void> {
    MainActivity context;
    private int id;
    public FindSongByIDAsycTask(MainActivity context, int id) {
        this.context = context;
        this.id=id;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onProgressUpdate(Song... values) {
        if (values[0] != null)
            context.getListSong().add(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        return null;
    }
}
