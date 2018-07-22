package com.example.dragon.project_cuoi_ki_android.offlineMusic.music;

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
import com.example.dragon.project_cuoi_ki_android.model.Song;

import java.util.ArrayList;
import java.util.List;

public class ListViewMusicAdapter extends ArrayAdapter<Song> {

    private Context context;
    private int resource;
    private List<Song> arrSong;

    public ListViewMusicAdapter(Context context, int resource, ArrayList<Song> arrSong) {
        super(context, resource, arrSong);
        this.context = context;
        this.resource = resource;
        this.arrSong = arrSong;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvSongPicture = (ImageView) convertView.findViewById(R.id.tvSongPicture);
            viewHolder.tvSongName = (TextView) convertView.findViewById(R.id.tvSongName);
            viewHolder.tvSongDuration = (TextView) convertView.findViewById(R.id.tvSongDuration);
            viewHolder.tvArtist = (TextView) convertView.findViewById(R.id.tvArtist);
            viewHolder.tvSongType = (TextView) convertView.findViewById(R.id.tvSongType);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Song song = arrSong.get(position);
        viewHolder.tvSongName.setText(song.getTitle());
        viewHolder.tvSongType.setText(song.getType());
        double durationMinute =song.getDuration()/60000.0;
        String duration =(int)durationMinute+":"+Math.round((durationMinute - (int)durationMinute)*60);
        viewHolder.tvSongDuration.setText(duration);
        if(song.getPicture()!=null) {
            viewHolder.tvSongPicture.setImageBitmap(song.getPicture());
        }else{
            viewHolder.tvSongPicture.setImageResource(R.mipmap.music_picture_default);
        }
        viewHolder.tvArtist.setText(song.getArtist());
        return convertView;
    }

    public class ViewHolder {
        TextView tvSongName, tvSongType, tvArtist, tvSongDuration;
        ImageView tvSongPicture;
    }
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
