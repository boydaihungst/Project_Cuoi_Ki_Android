package com.example.dragon.project_cuoi_ki_android.offlineMusic.artist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.model.Artist;
import com.example.dragon.project_cuoi_ki_android.model.Song;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.music.ListViewMusicAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListViewArtistAdapter extends ArrayAdapter<Artist> {
    private Context context;
    private int resource;
    private List<Artist> arrArtist;

    public ListViewArtistAdapter(Context context, int resource, ArrayList<Artist> arrArtist) {
        super(context, resource, arrArtist);
        this.context = context;
        this.resource = resource;
        this.arrArtist = arrArtist;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ListViewArtistAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            viewHolder = new ListViewArtistAdapter.ViewHolder();
            viewHolder.tvAristIcon = (TextView) convertView.findViewById(R.id.tvAristIcon);
            viewHolder.tvAristTitle = (TextView) convertView.findViewById(R.id.tvAristTitle);
            viewHolder.tvAristNumOfSong = (TextView) convertView.findViewById(R.id.tvAristNumOfSong);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ListViewArtistAdapter.ViewHolder) convertView.getTag();
        }
        Artist artist = arrArtist.get(position);
        viewHolder.tvAristIcon.setText(artist.getName().charAt(0)+"");
        viewHolder.tvAristTitle.setText(artist.getName());
        viewHolder.tvAristNumOfSong.setText(artist.getNumOfSong()+"");
        return convertView;
    }

    public class ViewHolder {
        TextView tvAristIcon, tvAristTitle, tvAristNumOfSong;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
