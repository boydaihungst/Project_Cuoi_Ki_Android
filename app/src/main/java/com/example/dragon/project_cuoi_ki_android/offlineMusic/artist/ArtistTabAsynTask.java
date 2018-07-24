package com.example.dragon.project_cuoi_ki_android.offlineMusic.artist;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.model.Artist;

import java.util.ArrayList;

public class ArtistTabAsynTask extends AsyncTask<Integer,Artist,Integer> {
    private ArrayList<Artist> listArtist = new ArrayList<>();
    private ArtistTabFragment context;

    public ArtistTabAsynTask(ArtistTabFragment context) {
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        listArtist = new ArrayList<>();
        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getActivity().getContentResolver().query(uri, null, null, null, MediaStore.Audio.Artists.ARTIST +" Asc");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
                    String numOfSong = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
                    Artist a = new Artist();
                    a.setId(Integer.parseInt(id));
                    a.setName(title);
                    a.setNumOfSong(Integer.parseInt(numOfSong));
                    publishProgress(a);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        context.setListArtist(listArtist);
        context.setArrayAdapter(new ListViewArtistAdapter(context.getActivity(), R.layout.list_view_cell_artist, context.getListArtist()));
        ArrayAdapter arrayAdapter = context.getArrayAdapter();
        arrayAdapter.setNotifyOnChange(true);
        context.getLvArtist().setAdapter(arrayAdapter);
        super.onPostExecute(integer);
    }

    @Override
    protected void onProgressUpdate(Artist... values) {
        listArtist.add(values[0]);
        super.onProgressUpdate(values);
    }

}
