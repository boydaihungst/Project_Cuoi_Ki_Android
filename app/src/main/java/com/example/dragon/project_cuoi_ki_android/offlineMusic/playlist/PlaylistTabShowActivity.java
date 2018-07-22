package com.example.dragon.project_cuoi_ki_android.offlineMusic.playlist;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.dragon.project_cuoi_ki_android.Controller.FragmentBroadcast;
import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.model.Artist;
import com.example.dragon.project_cuoi_ki_android.model.Playlist;
import com.example.dragon.project_cuoi_ki_android.model.Song;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.artist.ArtistTabShowAsyncTask;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.music.ListViewMusicAdapter;

import java.util.ArrayList;

public class PlaylistTabShowActivity  extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private ListView lvMusic;
    private ArrayList<Song> listSong = new ArrayList<>();
    private ArrayAdapter arrayAdapter;
    private Playlist playlist;
    private FragmentBroadcast broadcast;
    private Button btnBack;

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

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public FragmentBroadcast getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(FragmentBroadcast broadcast) {
        this.broadcast = broadcast;
    }

    public Button getBtnBack() {
        return btnBack;
    }

    public void setBtnBack(Button btnBack) {
        this.btnBack = btnBack;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_tab_show);
        Intent i =getIntent();
        playlist=i.getParcelableExtra("playlistId");
        broadcast = new FragmentBroadcast(this);
        lvMusic = findViewById(R.id.artist_show_list_view);
        btnBack = (Button)findViewById(R.id.artist_show_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        refreshFragment();
    }
    public void refreshFragment() {
        listSong.clear();
        arrayAdapter = new ListViewMusicAdapter(this, R.layout.list_view_cell_music, listSong);
        arrayAdapter.setNotifyOnChange(true);
        lvMusic.setAdapter(arrayAdapter);
        registerForContextMenu(lvMusic);
        lvMusic.setOnItemClickListener(this);
        //        loadTab();
        PlaylistTabShowAsyncTask tabShowAsyncTask = new PlaylistTabShowAsyncTask(this);
        //Gọi hàm execute để kích hoạt tiến trình
        tabShowAsyncTask.execute(playlist.getId());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        ArrayList<Song> listSong = new ArrayList<>();
        for (Song _song:this.listSong) {
            Song s = new Song();
            s.setUrl(_song.getUrl());
            s.setId(_song.getId());
            listSong.add(s);
        }
        bundle.putParcelableArrayList(FragmentBroadcast.ADD_ALL_SONG_NOW_PLAYING, listSong);
        broadcast.send(FragmentBroadcast.ADD_ALL_SONG_NOW_PLAYING, bundle);
    }

}
