package com.example.dragon.project_cuoi_ki_android.player;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dragon.project_cuoi_ki_android.Controller.ClientReceiver;
import com.example.dragon.project_cuoi_ki_android.Controller.FragmentBroadcast;
import com.example.dragon.project_cuoi_ki_android.Controller.ServiceReceiver;
import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.model.Song;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.music.ListViewMusicAdapter;

import java.util.ArrayList;

public class PlayerPlaylistTabFragment extends Fragment implements PlayerPlaylistTabRecyclerViewAdapter.PlayerItemClickListener {
    private RecyclerView recyclerView;
    private PlayerPlaylistTabRecyclerViewAdapter adapter;
    private ArrayList<Song> listSong;
    private FragmentBroadcast receiver;
    private View lastPlayCell;

    public PlayerPlaylistTabFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player_first_tab, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.player_playlist_list_view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        listSong = new ArrayList<>();
        adapter = new PlayerPlaylistTabRecyclerViewAdapter(getActivity(), R.layout.list_view_cell_music, listSong);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.requestFocus();
        receiver = new FragmentBroadcast(getContext());
        return view;
    }

    public void toggleColor(View view, int colorId) {
        if (view == null) return;
        TextView title = (TextView) view.findViewById(R.id.tvSongName);
        TextView artist = (TextView) view.findViewById(R.id.tvArtist);
        TextView duration = (TextView) view.findViewById(R.id.tvSongDuration);
        TextView type = (TextView) view.findViewById(R.id.tvSongType);
        title.setTextColor(ContextCompat.getColor(getContext(), colorId));
        artist.setTextColor(ContextCompat.getColor(getContext(), colorId));
        duration.setTextColor(ContextCompat.getColor(getContext(), colorId));
        type.setTextColor(ContextCompat.getColor(getContext(), colorId));
        lastPlayCell = view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listSong.clear();
        adapter.notifyDataSetChanged();
        Song s = new Song();
        s.setTitle("");
        s.setArtist("");
        s.setDuration(0);
        s.setTitle("");
        listSong.add(s);
        adapter.notifyItemInserted(listSong.indexOf(s));
    }

    public void setData(Song song, String action) {
        switch (action) {
            case ServiceReceiver.DELETE_ONE_FROM_LIST_SONG: {
                int i = listSong.indexOf(song);
                listSong.remove(song);
                adapter.notifyItemRemoved(i);
                return;
            }
            case ServiceReceiver.APEEND_LIST_SONG: {
                listSong.add(song);
                adapter.notifyItemInserted(listSong.indexOf(song));
                Log.d("append","xx");
                return;
            }
            case ServiceReceiver.DELETE_ALL_FROM_LIST_SONG: {
                listSong.clear();
                adapter.notifyDataSetChanged();
                Log.d("delete","xx");
                return;
            }
            case ServiceReceiver.PLAY: {
                recyclerView.getLayoutManager().generateDefaultLayoutParams();
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    if (adapter.getItem(i).equals(song)) {
                        View view = recyclerView.getLayoutManager().findViewByPosition(i);
                        toggleColor(lastPlayCell, R.color.white);
                        toggleColor(view, R.color.colorAccent);
                        }
                }
                return;
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        toggleColor(lastPlayCell, R.color.white);
        toggleColor(view, R.color.colorAccent);
        Bundle bundle = new Bundle();
        Song temp = adapter.getItem(position);
        Song s = new Song();
        s.setId(temp.getId());
        s.setUrl(temp.getUrl());
        bundle.putParcelable(ClientReceiver.PLAY, s);
        receiver.send(ClientReceiver.PLAY, bundle);
    }
}
