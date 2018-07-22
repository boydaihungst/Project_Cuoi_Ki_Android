package com.example.dragon.project_cuoi_ki_android.offlineMusic.playlist;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.Utils.Utils;
import com.example.dragon.project_cuoi_ki_android.model.Artist;
import com.example.dragon.project_cuoi_ki_android.model.Playlist;
import com.example.dragon.project_cuoi_ki_android.model.Song;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.music.ListViewMusicAdapter;

import java.io.File;
import java.util.ArrayList;

public class PlaylistTabFragment extends Fragment implements
        AdapterView.OnItemLongClickListener
        ,AdapterView.OnItemClickListener{
    private ListView lvPlaylist;
    private ArrayList<Playlist> playList = new ArrayList<>();
    private ArrayAdapter arrayAdapter;
    private Playlist longPressItem;

    public PlaylistTabFragment() {
    }

    public ListView getLvPlaylist() {
        return lvPlaylist;
    }

    public void setLvPlaylist(ListView lvPlaylist) {
        this.lvPlaylist = lvPlaylist;
    }

    public ArrayList<Playlist> getPlayList() {
        return playList;
    }

    public void setPlayList(ArrayList<Playlist> playList) {
        this.playList = playList;
    }

    public ArrayAdapter getArrayAdapter() {
        return arrayAdapter;
    }

    public void setArrayAdapter(ArrayAdapter arrayAdapter) {
        this.arrayAdapter = arrayAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playlist_fragment, container, false);
        lvPlaylist = view.findViewById(R.id.lvPlaylist);
        refreshFragment();
        // Register the ListView  for Context menu
        registerForContextMenu(lvPlaylist);
        lvPlaylist.setOnItemLongClickListener(this);
        lvPlaylist.setOnItemClickListener(this);
        return view;
    }

    public MenuInflater getMenuInflater() {
        return new MenuInflater(getActivity());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.playlist_tab_context_menu, menu);
        if (longPressItem != null)
            menu.setHeaderTitle(longPressItem.getTitle());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.playlist_tab_rename:
                showRenameDialog();
                return true;
            case R.id.playlist_tab_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete " + item.getTitle() + " ?");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletePlaylist();
                            }
                        })
                        .setNegativeButton("NO",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                        .create()
                        .show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    public void deletePlaylist(){
        if (longPressItem != null) {
            Uri playlists = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
            Uri deleteUri = ContentUris.withAppendedId(playlists, longPressItem.getId());
            getActivity().getContentResolver().delete(deleteUri, null, null);

            //update ui
            playList.remove(longPressItem);
            arrayAdapter.notifyDataSetChanged();
        }

    }
    public void showRenameDialog(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setTitle("Rename playlist");
        View viewInflated = inflater.inflate(R.layout.input_playlist_name_dialog, null);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        builder.setView(viewInflated);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String newPlaylistName = input.getText().toString().trim();
                if (longPressItem != null) {
                    ContentValues values = new ContentValues(1);
                    values.put(MediaStore.Audio.Playlists.NAME, newPlaylistName);
                    getActivity().getContentResolver().update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                            values, "_id=" + longPressItem.getId(), null);
                    //update UI
                    longPressItem.setTitle(newPlaylistName);
                    arrayAdapter.notifyDataSetChanged();
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
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        longPressItem = playList.get(position);
        return false;
    }

    public void refreshFragment() {
        playList.clear();
        PlaylistTabAsynTask playlistTabAsynTask = new PlaylistTabAsynTask(this);
        playlistTabAsynTask.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(getContext(),PlaylistTabShowActivity.class);
        i.putExtra("playlistId",playList.get(position));
        startActivity(i);
    }
}

