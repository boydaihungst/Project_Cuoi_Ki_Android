package com.example.dragon.project_cuoi_ki_android.offlineMusic.album;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.model.Album;

import java.util.ArrayList;

public class AlbumTabFragment extends Fragment implements RecycleViewCustomAdapter.ItemClickListener{
    private RecyclerView rvAlbum;
    private ArrayList<Album> listAlbum = new ArrayList<>();
    private RecycleViewCustomAdapter adapter;
    public AlbumTabFragment() {
    }

    public RecycleViewCustomAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(RecycleViewCustomAdapter adapter) {
        this.adapter = adapter;
    }

    public RecyclerView getRvAlbum() {
        return rvAlbum;
    }

    public void setRvAlbum(RecyclerView rvAlbum) {
        this.rvAlbum = rvAlbum;
    }

    public ArrayList<Album> getListAlbum() {
        return listAlbum;
    }

    public void setListAlbum(ArrayList<Album> listAlbum) {
        this.listAlbum = listAlbum;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.album_fragment, container, false);
        // set up the RecyclerView
        rvAlbum = view.findViewById(R.id.rvAlbum);
        AlbumTabAsynTask albumTabAsynTask = new AlbumTabAsynTask(this);
        albumTabAsynTask.execute();
        rvAlbum.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        
    }
}
