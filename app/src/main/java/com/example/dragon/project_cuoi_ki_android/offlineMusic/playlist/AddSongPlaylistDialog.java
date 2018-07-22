package com.example.dragon.project_cuoi_ki_android.offlineMusic.playlist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.Utils.Utils;
import com.example.dragon.project_cuoi_ki_android.model.Playlist;
import com.example.dragon.project_cuoi_ki_android.model.Song;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AddSongPlaylistDialog extends DialogFragment {
    private Button btnAddNew;
    private ArrayList<Playlist> listPlaylist;
    private ArrayAdapter<Playlist> adapter;
    private Song song;
    private Context c;

    public AddSongPlaylistDialog() {
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        c = getActivity();
        getAllPlaylist();
        adapter = new ArrayAdapter<Playlist>(getActivity(), R.layout.text_view, listPlaylist);
        adapter.setNotifyOnChange(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        //header
        LayoutInflater inflater = getActivity().getLayoutInflater();
        //title
        View customHeader = inflater.inflate(R.layout.add_playlit_dialog_header, null);
        TextView title = (TextView) customHeader.findViewById(R.id.playlist_dialog_title);
        title.setText("Playlist");
        builder.setCustomTitle(customHeader);
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {   //click add new btn
                    dialog.cancel();
                    inputPlaylistDialog();
                } else {
                    dialog.cancel();
                    Playlist pl = listPlaylist.get(which);
                    Object[] obj = addPlaylist(pl.getTitle());
                    if (obj != null) {
                        long playlistId = (long) obj[0];
                        ArrayList<Song> temp = new ArrayList<>();
                        temp.add(song);
                        Utils u = new Utils(c);
                        u.addTracksToPlaylist(playlistId, temp, c, (int) obj[1]);
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        Dialog dia = builder.create();
        return dia;
    }

    public void inputPlaylistDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setTitle("Add new playlist");
        View viewInflated = inflater.inflate(R.layout.input_playlist_name_dialog, null);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        builder.setView(viewInflated);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String pName = input.getText().toString().trim();
                Object[] obj = addPlaylist(pName);
                if (obj != null) {
                    long playlistId = (long) obj[0];
                    ArrayList<Song> temp = new ArrayList<>();
                    temp.add(song);
                    Utils u = new Utils(c);
                    u.addTracksToPlaylist(playlistId, temp, c, (int) obj[1]);
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void getAllPlaylist() {
        listPlaylist = new ArrayList<>();
        listPlaylist.add(new Playlist(-99, "Add new playlist", 0));
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, MediaStore.Audio.Playlists.NAME + " Asc");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME));
                    Playlist pl = new Playlist();
                    pl.setId(Integer.parseInt(id));
                    pl.setTitle(title);
                    listPlaylist.add(pl);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    public Object[] addPlaylist(String pname) {
        Uri playlists = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor c = this.c.getContentResolver().query(playlists, new String[]{"*"}, null, null,
                null);
        long playlistId = 0;
        int pos = 0;
        while (c.moveToNext()) {
            String plname = c.getString(c
                    .getColumnIndex(MediaStore.Audio.Playlists.NAME));
            if (plname.equalsIgnoreCase(pname)) {
                playlistId = c.getLong(c
                        .getColumnIndex(MediaStore.Audio.Playlists._ID));
            }
            ++pos;
        }
        c.close();
        if (playlistId != 0) {
            Object[] obj = new Object[2];
            obj[0] = playlistId;
            obj[1] = pos;
            return obj;
        } else {
//        Log.d(TAG, "CREATING PLAYLIST: " + pname);
            ContentValues v1 = new ContentValues();
            v1.put(MediaStore.Audio.Playlists.NAME, pname);
            v1.put(MediaStore.Audio.Playlists.DATE_MODIFIED,
                    System.currentTimeMillis());
            Uri newpl = this.c.getContentResolver().insert(playlists, v1);
            return addPlaylist(pname);
        }
    }
}
