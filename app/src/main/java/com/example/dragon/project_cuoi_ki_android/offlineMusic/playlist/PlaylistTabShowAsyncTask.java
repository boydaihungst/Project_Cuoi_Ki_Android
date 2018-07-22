package com.example.dragon.project_cuoi_ki_android.offlineMusic.playlist;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.TextView;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.Utils.Utils;
import com.example.dragon.project_cuoi_ki_android.model.Song;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.artist.ArtistTabShowActivity;

import java.util.ArrayList;

public class PlaylistTabShowAsyncTask extends  AsyncTask<Integer,Song,Void> {
    private PlaylistTabShowActivity context;
    private ArrayList<Song> listSong = new ArrayList<>();

    public PlaylistTabShowAsyncTask(PlaylistTabShowActivity context) {
        this.context = context;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        TextView title = (TextView)context.findViewById(R.id.artist_show_title_album);
        title.setText(context.getPlaylist().getTitle());
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
        Cursor cursor =Utils.getPlaylistTracks(context,Long.valueOf(context.getPlaylist().getId()));
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
//                    String _id = MediaStore.Audio.Playlists.Members._ID;
//                    String audio_id = MediaStore.Audio.Playlists.Members.AUDIO_ID;
//                    String artist = MediaStore.Audio.Playlists.Members.ARTIST;
//                    String album = MediaStore.Audio.Playlists.Members.ALBUM;
//                    String album_id = MediaStore.Audio.Playlists.Members.ALBUM_ID;
//                    String title = MediaStore.Audio.Playlists.Members.TITLE;
//                    String duration = MediaStore.Audio.Playlists.Members.DURATION;
//                    String location = MediaStore.Audio.Playlists.Members.DATA;
//                    String composer = MediaStore.Audio.Playlists.Members.COMPOSER;
//                    String playorder = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
                    String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));
                    String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION));
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
