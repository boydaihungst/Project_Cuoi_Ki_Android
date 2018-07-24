package com.example.dragon.project_cuoi_ki_android.offlineMusic.music;


import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dragon.project_cuoi_ki_android.Controller.FragmentBroadcast;
import com.example.dragon.project_cuoi_ki_android.Controller.ServiceReceiver;
import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.Utils.Utils;
import com.example.dragon.project_cuoi_ki_android.model.Song;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.playlist.AddSongPlaylistDialog;
import com.example.dragon.project_cuoi_ki_android.player.PlayerPlaylistTabFragment;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicTabFragment extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private ListView lvMusic;
    private ArrayList<Song> listSong = new ArrayList<>();
    private Song longPressItem;
    private ArrayAdapter arrayAdapter;
    private FragmentBroadcast broadcast;
    private AsyncTask sendAllSongTask;

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

    dataTransaction listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_fragment, container, false);
        lvMusic = view.findViewById(R.id.lvMusic);
        refreshFragment();
        // Register the ListView  for Context menu
        return view;
    }

    public interface dataTransaction {
        void playThisSong(Song song);

        void updateListSongDB(Song listSongInDB);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof dataTransaction) {
            listener = (dataTransaction) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement interface");
        }
    }

    public MenuInflater getMenuInflater() {
        return new MenuInflater(getActivity());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        broadcast = new FragmentBroadcast(getContext());
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.music_tab_context_menu, menu);
        if (longPressItem != null)
            menu.setHeaderTitle(longPressItem.getTitle());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.musicTabAddToPlaylist: {
                AddSongPlaylistDialog dialogFragment = new AddSongPlaylistDialog();
                dialogFragment.setSong(longPressItem);
                dialogFragment.show(getActivity().getFragmentManager(), "addplaylist");
                return true;
            }
            case R.id.musicTabAddAllToNowPlaying: {
                if (sendAllSongTask != null && sendAllSongTask.getStatus() == AsyncTask.Status.RUNNING && sendAllSongTask.isCancelled()) {
                    sendAllSongTask.cancel(true);
                }
                sendAllSongTask = new AsyncTask<Void, Void, Void>() {
                    ArrayList<Song> tempListSong = new ArrayList<>();

                    @Override
                    protected Void doInBackground(Void... params) {
                        for (Song _song : listSong) {
                            Song s = new Song();
                            s.setUrl(_song.getUrl());
                            s.setId(_song.getId());
                            tempListSong.add(s);
                        }
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(FragmentBroadcast.ADD_ALL_SONG_NOW_PLAYING, tempListSong);
                        broadcast.send(FragmentBroadcast.ADD_ALL_SONG_NOW_PLAYING, bundle);
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                return true;
            }
            case R.id.musicTabAddToNowPlaying: {
                Bundle bundle = new Bundle();
                Song s = new Song();
                s.setUrl(longPressItem.getUrl());
                s.setId(longPressItem.getId());
                bundle.putParcelable(FragmentBroadcast.ADD_SONG_NOW_PLAYING, s);
                broadcast.send(FragmentBroadcast.ADD_SONG_NOW_PLAYING, bundle);
                return true;
            }
            case R.id.musicTabDeleteMusic: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete " + longPressItem.getTitle() + " ?");
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
                                        File songNeedDelete = new File(longPressItem.getUrl());
                                        if (songNeedDelete.exists()) {
                                            //remove in Sdcard
                                            if (songNeedDelete.delete()) {
                                                //broadcast la file bi xoa roi
                                                Bundle bundle = new Bundle();
                                                Song s = new Song();
                                                s.setUrl(longPressItem.getUrl());
                                                s.setId(longPressItem.getId());
                                                bundle.putParcelable(FragmentBroadcast.DELETE_SONG_IN_DB, s);
                                                broadcast.send(FragmentBroadcast.DELETE_SONG_IN_DB, bundle);
                                                listSong.remove(longPressItem);
                                                arrayAdapter.remove(longPressItem);
                                                arrayAdapter.notifyDataSetChanged();
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
            }
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        longPressItem = listSong.get(position);
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Song s = new Song();
        Song selected = listSong.get(position);
        s.setId(selected.getId());
        s.setUrl(selected.getUrl());
        listener.playThisSong(s);
    }

    public void refreshFragment() {
        listSong.clear();
        arrayAdapter = new ListViewMusicAdapter(getActivity(), R.layout.list_view_cell_music, listSong);
        arrayAdapter.setNotifyOnChange(true);
        lvMusic.setAdapter(arrayAdapter);
        registerForContextMenu(lvMusic);
        lvMusic.setOnItemLongClickListener(this);
        lvMusic.setOnItemClickListener(this);
        //        loadTab();
        MusicTabAsynTask musicTabAsynTask = new MusicTabAsynTask(this);
        //Gọi hàm execute để kích hoạt tiến trình
        musicTabAsynTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

}
