package com.example.dragon.project_cuoi_ki_android.offlineMusic.music;


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

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicTabFragment extends Fragment implements AdapterView.OnItemLongClickListener {
    private ListView lvMusic;
    private ArrayList<Song> listSong = new ArrayList<>();
    private Song longPressItem;
    private ArrayAdapter arrayAdapter;

    public MusicTabFragment() {
    }

    public ListView getLvMusic() {
        return lvMusic;
    }

    public void setLvMusic(ListView lvMusic) {
        this.lvMusic = lvMusic;
    }

    public ArrayList<Song> getListSong() {
        return listSong;
    }

    public void setListSong(ArrayList<Song> listSong) {
        this.listSong = listSong;
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
        View view = inflater.inflate(R.layout.music_fragment, container, false);
        lvMusic = view.findViewById(R.id.lvMusic);
//        loadTab();
        MusicTabAsynTask musicTabAsynTask = new MusicTabAsynTask(this);
        //Gọi hàm execute để kích hoạt tiến trình
        musicTabAsynTask.execute();
        // Register the ListView  for Context menu

        return view;
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
                                if (longPressItem != null) {
                                    //remove in database
                                    int row = getActivity().getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                            MediaStore.Audio.Media._ID + " = " + longPressItem.getId(), null);
                                    if (row != 0) {
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
