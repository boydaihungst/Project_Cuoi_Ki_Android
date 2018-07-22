package com.example.dragon.project_cuoi_ki_android.offlineMusic.artist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.dragon.project_cuoi_ki_android.Controller.FragmentBroadcast;
import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.model.Album;
import com.example.dragon.project_cuoi_ki_android.model.Artist;
import com.example.dragon.project_cuoi_ki_android.model.Song;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.album.AlbumTabShowAsyncTask;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.music.ListViewMusicAdapter;

import java.util.ArrayList;

public class ArtistTabShowActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView lvMusic;
    private ArrayList<Song> listSong = new ArrayList<>();
    private Song longPressItem;
    private ArrayAdapter arrayAdapter;
    private Artist artist;
    private FragmentBroadcast broadcast;
    private Button btnBack;

    public ArtistTabShowActivity() {
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

    public Song getLongPressItem() {
        return longPressItem;
    }

    public void setLongPressItem(Song longPressItem) {
        this.longPressItem = longPressItem;
    }

    public ArrayAdapter getArrayAdapter() {
        return arrayAdapter;
    }

    public void setArrayAdapter(ArrayAdapter arrayAdapter) {
        this.arrayAdapter = arrayAdapter;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_tab_show);
        Intent i = getIntent();
        artist = i.getParcelableExtra("artist");
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
        ArtistTabShowAsyncTask tabShowAsyncTask = new ArtistTabShowAsyncTask(this);
        //Gọi hàm execute để kích hoạt tiến trình
        tabShowAsyncTask.execute(artist.getId());
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
