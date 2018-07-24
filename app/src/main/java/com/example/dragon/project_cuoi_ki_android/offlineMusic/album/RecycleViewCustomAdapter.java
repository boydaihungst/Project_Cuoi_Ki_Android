package com.example.dragon.project_cuoi_ki_android.offlineMusic.album;

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
import com.example.dragon.project_cuoi_ki_android.model.Album;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecycleViewCustomAdapter extends RecyclerView.Adapter<RecycleViewCustomAdapter.ViewHolder> {

    private Context context;
    private int resource;
    private List<Album> arrAlbum;
    private ItemClickListener mClickListener;
    private LayoutInflater mInflater;
    public RecycleViewCustomAdapter(Context context, int resource, ArrayList<Album> arrAlbum) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.arrAlbum = arrAlbum;
        this.resource = resource;
    }

    public List<Album> getArrAlbum() {
        return arrAlbum;
    }

    public void addAlbum(Album Album) {
        this.arrAlbum.add(Album);
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(resource, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the imageView.... in each cell
    @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            File imgFile = new File(arrAlbum.get(position).getPicture());
            Bitmap picture = null;
            if (imgFile.exists()) {
                picture = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }
            System.out.println(holder);
            if (picture != null) {
                holder.ivAlbum.setImageBitmap(picture);
            } else {
                holder.ivAlbum.setImageResource(R.mipmap.music_picture_default);
            }
            holder.tvAlbumTitle.setText(arrAlbum.get(position).getTitle());
            holder.tvAlbumNumSong.setText(arrAlbum.get(position).getNumAlbum()+" Tracks");
            holder.tvAlbumArtist.setText(arrAlbum.get(position).getArtist());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return arrAlbum.size();
    }


    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivAlbum;
        TextView tvAlbumTitle, tvAlbumNumSong, tvAlbumArtist;

        ViewHolder(View itemView) {
            super(itemView);
            this.ivAlbum = (ImageView) itemView.findViewById(R.id.ivAlbum);
            this.tvAlbumTitle = (TextView) itemView.findViewById(R.id.tvAlbumTitle);
            this.tvAlbumNumSong = (TextView) itemView.findViewById(R.id.tvAlbumNumSong);
            this.tvAlbumArtist = (TextView) itemView.findViewById(R.id.tvAlbumArtist);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, this.getAdapterPosition());
        }
    }

    public Album getItem(int id) {
        return arrAlbum.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
