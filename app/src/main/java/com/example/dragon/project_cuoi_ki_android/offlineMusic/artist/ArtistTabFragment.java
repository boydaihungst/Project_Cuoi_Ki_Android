package com.example.dragon.project_cuoi_ki_android.offlineMusic.artist;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.model.Artist;
import com.example.dragon.project_cuoi_ki_android.model.Song;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.album.AlbumTabAsynTask;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.album.AlbumTabShowActivity;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.music.ListViewMusicAdapter;

import java.util.ArrayList;

public class ArtistTabFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ListView lvArtist;
    private ArrayList<Artist> listArtist = new ArrayList<>();
    private ArrayAdapter arrayAdapter;

    public ArtistTabFragment() {
    }

    public ListView getLvArtist() {
        return lvArtist;
    }

    public void setLvArtist(ListView lvArtist) {
        this.lvArtist = lvArtist;
    }

    public ArrayList<Artist> getListArtist() {
        return listArtist;
    }

    public void setListArtist(ArrayList<Artist> listArtist) {
        this.listArtist = listArtist;
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
        View view = inflater.inflate(R.layout.artist_fragment, container, false);
        lvArtist = view.findViewById(R.id.lvArtist);
        refeshFragment();
        lvArtist.setOnItemClickListener(this);
        // Register the ListView  for Context menu
        return view;
    }
    public void refeshFragment(){
        listArtist.clear();
        ArtistTabAsynTask artistTabAsynTask =new ArtistTabAsynTask(this);
        artistTabAsynTask.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(getContext(),ArtistTabShowActivity.class);
        i.putExtra("artist",listArtist.get(position));
        startActivity(i);
    }
}

