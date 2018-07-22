package com.example.dragon.project_cuoi_ki_android.offlineMusic.album;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.dragon.project_cuoi_ki_android.Controller.FragmentBroadcast;
import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.model.Album;
import com.example.dragon.project_cuoi_ki_android.model.Song;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.music.ListViewMusicAdapter;

import java.util.ArrayList;

public class AlbumTabShowActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView lvMusic;
    private ArrayList<Song> listSong = new ArrayList<>();
    private Song longPressItem;
    private ArrayAdapter arrayAdapter;
    private  Album album;
    private FragmentBroadcast broadcast;
    private Button btnBack;
    public AlbumTabShowActivity() {
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

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_show_activity);
        Intent i = getIntent();
        album = i.getParcelableExtra("album");
        broadcast = new FragmentBroadcast(this);
        lvMusic = findViewById(R.id.album_show_list_view);
        btnBack = (Button)findViewById(R.id.album_show_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             finish();
            }
        });
        refreshFragment();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void refreshFragment() {
        listSong.clear();
        arrayAdapter = new ListViewMusicAdapter(this, R.layout.list_view_cell_music, listSong);
        arrayAdapter.setNotifyOnChange(true);
        lvMusic.setAdapter(arrayAdapter);
        registerForContextMenu(lvMusic);
        lvMusic.setOnItemClickListener(this);
        //        loadTab();
        AlbumTabShowAsyncTask albumTabShowAsyncTask = new AlbumTabShowAsyncTask(this);
        //Gọi hàm execute để kích hoạt tiến trình
        albumTabShowAsyncTask.execute(album.getId());
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
