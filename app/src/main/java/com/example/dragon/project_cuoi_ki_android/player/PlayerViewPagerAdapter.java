package com.example.dragon.project_cuoi_ki_android.player;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.model.Song;

public class PlayerViewPagerAdapter extends FragmentStatePagerAdapter{
    private Context context;
    private Fragment[] tabFragment;
    private int numTab;

    public PlayerViewPagerAdapter(FragmentManager fm, int numTab, Context c) {
        super(fm);
        context = c;
        this.numTab = numTab;
        tabFragment = new Fragment[numTab];
    }

    public Fragment[] getTabFragment() {
        return tabFragment;
    }

    public void setTabFragment(Fragment[] tabFragment) {
        this.tabFragment = tabFragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                PlayerPlaylistTabFragment playerPlaylistTabFragment = new PlayerPlaylistTabFragment();
                tabFragment[0] = playerPlaylistTabFragment;
                return playerPlaylistTabFragment;
            }
            case 1: {
                PlayerTabFragment playerTabFragment = new PlayerTabFragment();
                tabFragment[1] = playerTabFragment;
                return playerTabFragment;
            }
            case 2: {
                PlayerLyricTabFragment lyricTabFragment = new PlayerLyricTabFragment();
                tabFragment[2] = lyricTabFragment;
                return lyricTabFragment;
            }
        }
        return new Fragment();
    }


    @Override
    public int getCount() {
        return numTab;
    }

    public int getItemPosition(Object item) {
        return POSITION_UNCHANGED;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    //main activity su dung ham nay de update song
    public void updateFirstFragment(Song song,String action) {
        try {
            ((PlayerPlaylistTabFragment) tabFragment[0]).setData(song,action);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    public void updateSecondFragment(Song song) {
        try {
            ((PlayerTabFragment) tabFragment[1]).setData(song, false);

        } catch (NullPointerException e) {

        }
    }
    public void updateThirdFragment(Song song) {
        try {
            ((PlayerLyricTabFragment) tabFragment[2]).setData(song);

        } catch (NullPointerException e) {

        }
    }

}
