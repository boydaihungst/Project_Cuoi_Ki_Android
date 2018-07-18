package com.example.dragon.project_cuoi_ki_android.history;


import android.content.DialogInterface;
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
import android.widget.ListView;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.model.Song;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.music.ListViewMusicAdapter;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryTabFragment extends Fragment implements AdapterView.OnItemLongClickListener {
    private ListView lvMusic;
    private ArrayList<Song> listSong = new ArrayList<>();
    private Song longPressItem;
    private ArrayAdapter arrayAdapter;

    public HistoryTabFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);
        lvMusic = view.findViewById(R.id.lvHistoryMusic);
        loadTab();
        // Register the ListView  for Context menu
        registerForContextMenu(lvMusic);
        lvMusic.setOnItemLongClickListener(this);
        return view;
    }

    private void loadTab() {
            listSong = new ArrayList<>();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
            Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, MediaStore.Audio.Media.TITLE +" Asc");
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
                        }catch (Exception e){
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
                        listSong.add(e);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            arrayAdapter = new ListViewMusicAdapter(getActivity(), R.layout.list_view_cell_music, listSong);
            arrayAdapter.setNotifyOnChange(true);
            lvMusic.setAdapter(arrayAdapter);
    }

    public MenuInflater getMenuInflater() {
        return new MenuInflater(getActivity());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.music_tab_context_menu, menu);
        if (longPressItem != null)
            menu.setHeaderTitle(longPressItem.getTitle());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.musicTabAddToPlaylist:
                String name = "";
                return true;
            case R.id.musicTabAddToNowPlaying:
                return true;
            case R.id.musicTabDeleteMusic:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete " + item.getTitle() + " ?");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(longPressItem!=null) {
                                    //remove in database
                                    int row = getActivity().getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                            MediaStore.Audio.Media._ID+" = "+longPressItem.getId(),null);
                                    if(row!=0) {
                                        listSong.remove(longPressItem);
                                        arrayAdapter.remove(longPressItem);
                                        arrayAdapter.notifyDataSetChanged();
                                        File songNeedDelete = new File(longPressItem.getUrl());
                                        if (songNeedDelete.exists()) {
                                            //remove in Sdcard
                                            if (songNeedDelete.delete()) {
                                            } else {
                                            }
                                        }
                                    }
                                }
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        longPressItem = listSong.get(position);
        return false;
    }
}
