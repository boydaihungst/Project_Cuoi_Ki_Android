package com.example.dragon.project_cuoi_ki_android.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.Utils.Utils;
import com.example.dragon.project_cuoi_ki_android.model.Song;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.album.RecycleViewCustomAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayerPlaylistTabRecyclerViewAdapter extends RecyclerView.Adapter<PlayerPlaylistTabRecyclerViewAdapter.ViewHolder> {


    private Context context;
    private int resource;
    private List<Song> arrSong;
    private PlayerItemClickListener mClickListener;
    private LayoutInflater mInflater;
    public PlayerPlaylistTabRecyclerViewAdapter(Context context, int resource, ArrayList<Song> arrSong) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.arrSong = arrSong;
        this.resource = resource;
    }

    public List<Song> getArrSong() {
        return arrSong;
    }

    public void addSong(Song Song) {
        this.arrSong.add(Song);
    }

    // inflates the cell layout from xml when needed
    @Override
    public PlayerPlaylistTabRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(resource, parent, false);
        return new PlayerPlaylistTabRecyclerViewAdapter.ViewHolder(view);
    }

    // binds the data to the imageView.... in each cell
    @Override
    public void onBindViewHolder(PlayerPlaylistTabRecyclerViewAdapter.ViewHolder holder, int position) {
        try {
            if (arrSong.get(position).getPicture() != null) {
                holder.tvSongPicture.setImageBitmap(arrSong.get(position).getPicture());
            } else {
                holder.tvSongPicture.setImageResource(R.mipmap.music_picture_default);
            }
            holder.tvSongName.setText(arrSong.get(position).getTitle());
            holder.tvArtist.setText(arrSong.get(position).getArtist());
            holder.tvSongDuration.setText(Utils.millisecondsToString(arrSong.get(position).getDuration()));
            holder.tvSongType.setText(arrSong.get(position).getType());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return arrSong.size();
    }


    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView tvSongPicture;
        TextView tvSongName, tvArtist, tvSongDuration,tvSongType;

        ViewHolder(View itemView) {
            super(itemView);
            this.tvSongPicture = (ImageView) itemView.findViewById(R.id.tvSongPicture);
            this.tvSongName = (TextView) itemView.findViewById(R.id.tvSongName);
            this.tvArtist = (TextView) itemView.findViewById(R.id.tvArtist);
            this.tvSongDuration = (TextView) itemView.findViewById(R.id.tvSongDuration);
            this.tvSongType = (TextView) itemView.findViewById(R.id.tvSongType);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, this.getAdapterPosition());
        }
    }

    public Song getItem(int id) {
        return arrSong.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(PlayerItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface PlayerItemClickListener {
        void onItemClick(View view, int position);
    }
}
