package com.example.dragon.project_cuoi_ki_android.offlineMusic.playlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public class ListViewPlaylistAdapter extends ArrayAdapter<Playlist> {
    private Context context;
    private int resource;
    private List<Playlist> arrPlaylist;

    public ListViewPlaylistAdapter(Context context, int resource, ArrayList<Playlist> arrPlaylist) {
        super(context, resource, arrPlaylist);
        this.context = context;
        this.resource = resource;
        this.arrPlaylist = arrPlaylist;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ListViewPlaylistAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            viewHolder = new ListViewPlaylistAdapter.ViewHolder();
            viewHolder.tvPlaylistIcon = (TextView) convertView.findViewById(R.id.tvPlaylistIcon);
            viewHolder.tvPlaylistTitle = (TextView) convertView.findViewById(R.id.tvPlaylistTitle);
            viewHolder.tvPlaylistNumOfSong = (TextView) convertView.findViewById(R.id.tvPlaylistNumOfSong);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ListViewPlaylistAdapter.ViewHolder) convertView.getTag();
        }
        Playlist Playlist = arrPlaylist.get(position);
        viewHolder.tvPlaylistIcon.setText(Playlist.getTitle().charAt(0)+"");
        viewHolder.tvPlaylistTitle.setText(Playlist.getTitle());
        viewHolder.tvPlaylistNumOfSong.setText(Playlist.getNumSong()+"");
        return convertView;
    }

    public class ViewHolder {
        TextView tvPlaylistIcon, tvPlaylistTitle, tvPlaylistNumOfSong;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
