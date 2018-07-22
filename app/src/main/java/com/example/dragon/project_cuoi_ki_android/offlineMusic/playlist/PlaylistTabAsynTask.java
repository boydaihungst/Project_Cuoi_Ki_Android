package com.example.dragon.project_cuoi_ki_android.offlineMusic.playlist;

import android.content.ContentResolver;
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
import com.example.dragon.project_cuoi_ki_android.Utils.Utils;
import com.example.dragon.project_cuoi_ki_android.model.Playlist;
import com.example.dragon.project_cuoi_ki_android.model.Song;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.music.MusicTabFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class PlaylistTabAsynTask extends AsyncTask<Integer, Playlist, Integer> {
    private PlaylistTabFragment context;
    private ArrayList<Playlist> playList = new ArrayList<>();

    public PlaylistTabAsynTask(PlaylistTabFragment context) {
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        playList = new ArrayList<>();
        HashMap<String,Integer> songCount = new HashMap<>();
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getActivity().getContentResolver().query(uri, null, null, null, MediaStore.Audio.Playlists.NAME + " Asc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME));
                Cursor cursorSong = Utils.getPlaylistTracks(this.context.getActivity(),Long.valueOf(id));
                int numOfSong=0;
                if(cursorSong!=null){
                    numOfSong = cursorSong.getCount();
                }
                Playlist pl = new Playlist();
                pl.setId(Integer.parseInt(id));
                pl.setTitle(title);
                pl.setNumSong(numOfSong);
                publishProgress(pl);
            }
            cursor.close();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        context.setPlayList(playList);
        context.setArrayAdapter(new ListViewPlaylistAdapter(context.getActivity(), R.layout.list_view_cell_playlist, context.getPlayList()));
        ArrayAdapter arrayAdapter = context.getArrayAdapter();
        arrayAdapter.setNotifyOnChange(true);
        context.getLvPlaylist().setAdapter(arrayAdapter);
        super.onPostExecute(integer);
    }

    @Override
    protected void onProgressUpdate(Playlist... values) {
        playList.add(values[0]);
        super.onProgressUpdate(values);
    }
}
