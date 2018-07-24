package com.example.dragon.project_cuoi_ki_android.offlineMusic.album;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.model.Album;

import java.util.ArrayList;

public class AlbumTabFragment extends Fragment implements RecycleViewCustomAdapter.ItemClickListener{
    private RecyclerView rvAlbum;
    private ArrayList<Album> listAlbum = new ArrayList<>();
    private RecycleViewCustomAdapter adapter;
    private AlbumTabShowActivity albumTabShowActivity;
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
    public void addListAlbum(Album album) {
        this.listAlbum.add(album);
//        adapter.addAlbum(album);
        adapter.notifyItemInserted(this.listAlbum.size()-1);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.album_fragment, container, false);
        // set up the RecyclerView
        rvAlbum = view.findViewById(R.id.rvAlbum);
        refreshFragment();
        rvAlbum.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent i = new Intent(getContext(),AlbumTabShowActivity.class);
        i.putExtra("album",listAlbum.get(position));
        startActivity(i);
    }
    public void refreshFragment(){
        listAlbum.clear();
        int numberOfColumns = 2;
        adapter=new RecycleViewCustomAdapter(getActivity(), R.layout.recycler_view_cell_album, listAlbum);
        rvAlbum.setHasFixedSize(false);
        rvAlbum.setAdapter(adapter);
        rvAlbum.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        adapter.setClickListener(this);
        AlbumTabAsynTask albumTabAsynTask = new AlbumTabAsynTask(this);
        albumTabAsynTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
