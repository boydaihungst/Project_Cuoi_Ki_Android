package com.example.dragon.project_cuoi_ki_android.player;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.model.Song;

public class PlayerLyricTabFragment extends Fragment{
    private TextView lyricView;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;

    public PlayerLyricTabFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player_third_tab, container, false);
        lyricView = (TextView) view.findViewById(R.id.player_view_lyric);
        lyricView.setMovementMethod(new ScrollingMovementMethod());
        return view;
    }

    public void setData(Song song) {
        StringBuilder temp = new StringBuilder();
        temp.append(song.getLyrics().trim());
        if (temp.length() <= 0) {
            lyricView.setText("Không có lyrics :D");
        } else
            lyricView.setText(song.getLyrics());
    }

}
