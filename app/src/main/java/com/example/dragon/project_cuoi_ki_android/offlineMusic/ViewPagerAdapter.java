package com.example.dragon.project_cuoi_ki_android.offlineMusic;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.album.AlbumTabFragment;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.artist.ArtistTabFragment;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.music.MusicTabFragment;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.playlist.PlaylistTabFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private String[] tabTitle;
    private int numTab;
    private Fragment[] tabFragment;
    private MusicTabFragment musicTabFragment;
    private AlbumTabFragment albumTabFragment;
    private ArtistTabFragment artistTabFragment;
    private PlaylistTabFragment playlistTabFragment;
    public ViewPagerAdapter(FragmentManager fm, int numTab, Context c) {
        super(fm);
        context = c;
        this.numTab = numTab;
        tabTitle = context.getResources().getStringArray(R.array.music_tab_title);
        tabFragment = new Fragment[numTab];
         musicTabFragment = new MusicTabFragment();
         albumTabFragment = new AlbumTabFragment();
         artistTabFragment = new ArtistTabFragment();
         playlistTabFragment = new PlaylistTabFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                tabFragment[0] = musicTabFragment;
                return musicTabFragment;
            }
            case 1: {
                tabFragment[1] = albumTabFragment;
                return albumTabFragment;
            }
            case 2: {
                tabFragment[2] = artistTabFragment;
                return artistTabFragment;
            }
            case 3: {
                tabFragment[3] = playlistTabFragment;
                return playlistTabFragment;
            }
        }
        return new Fragment();
    }

    @Override
    public int getCount() {
        return numTab;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        //return title cho tab
        return tabTitle[position];
    }

    public void updateFragmentExcept(int exceptFragment) {
        for (int i = 0; i < tabFragment.length; i++) {
            if (exceptFragment != i) {
                switch (i) {
                    case 0: {
                        ((MusicTabFragment) tabFragment[0]).refreshFragment();
                        break;
                    }
                    case 1: {
                        ((AlbumTabFragment) tabFragment[1]).refreshFragment();
                        break;
                    }
                    case 2: {
                        ((ArtistTabFragment) tabFragment[2]).refeshFragment();
                        break;
                    }
                    case 3: {
                        ((PlaylistTabFragment) tabFragment[3]).refreshFragment();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
