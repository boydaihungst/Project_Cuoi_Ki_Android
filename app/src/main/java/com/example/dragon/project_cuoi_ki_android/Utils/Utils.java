package com.example.dragon.project_cuoi_ki_android.Utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.example.dragon.project_cuoi_ki_android.Controller.FragmentBroadcast;
import com.example.dragon.project_cuoi_ki_android.model.Song;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private Context context;
    private FragmentBroadcast broadcast;
    public Utils(Context context) {
        this.context = context;
        broadcast= new FragmentBroadcast(context);
    }

    public Bitmap resize(Drawable image, View parent) {
        Bitmap b = ((BitmapDrawable) image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, parent.getWidth(), parent.getHeight(), false);
        return bitmapResized;
    }

    public static String getLyric(String urlSong) {
        try {
            AudioFile f = AudioFileIO.read(new File(urlSong));
            Tag tag = f.getTag();
            List<TagField> fields = tag.getFields(FieldKey.LYRICS);
            for (TagField field : fields) {
                return field.toString();
            }
        } catch (Exception e) {
        }
        return "";
    }

    public void addTracksToPlaylist(final long playlistId, ArrayList<Song> tracks, final Context context,int pos) {
        //tranh duplicate
        Cursor c = getPlaylistTracks(context,playlistId);
        while(c.moveToNext()){
            String songId = c.getString(c.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID));
            for (int i = 0; i < tracks.size(); i++) {
                if (Integer.parseInt(songId) == tracks.get(i).getId()){
                    tracks.remove(i);
                    break;
                }
            }
        }

        c.close();
        if(tracks.size()  <=0 ) return;
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        ContentResolver resolver = context.getContentResolver();
        ContentValues[] values = new ContentValues[tracks.size()];
        for (int i = 0; i < tracks.size(); i++) {
            values[i] = new ContentValues();
            values[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, i + pos + 1);
            values[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, tracks.get(i).getId());
            values[i].put(MediaStore.Audio.Playlists.Members.DATA, tracks.get(i).getUrl());
        }
        int num = resolver.bulkInsert(uri, values);
        resolver.notifyChange(Uri.parse("content://media"), null);
        broadcast.send(FragmentBroadcast.PLAYLIST_CHANGED,new Bundle());
    }
    public static Cursor getPlaylistTracks(Context context, Long playlist_id) {
        Uri newuri = MediaStore.Audio.Playlists.Members.getContentUri(
                "external", playlist_id);
        ContentResolver resolver = context.getContentResolver();
        String _id = MediaStore.Audio.Playlists.Members._ID;
        String audio_id = MediaStore.Audio.Playlists.Members.AUDIO_ID;
        String artist = MediaStore.Audio.Playlists.Members.ARTIST;
        String album = MediaStore.Audio.Playlists.Members.ALBUM;
        String album_id = MediaStore.Audio.Playlists.Members.ALBUM_ID;
        String title = MediaStore.Audio.Playlists.Members.TITLE;
        String duration = MediaStore.Audio.Playlists.Members.DURATION;
        String location = MediaStore.Audio.Playlists.Members.DATA;
        String composer = MediaStore.Audio.Playlists.Members.COMPOSER;
        String playorder = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
        String date_modified = MediaStore.Audio.Playlists.Members.DATE_MODIFIED;
        String[] columns = {_id, audio_id, artist, album_id, album, title, duration,
                location, date_modified, playorder, composer};
        return resolver.query(newuri, columns, null, null, null);

    }
}
