package com.example.dragon.project_cuoi_ki_android.offlineMusic;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.album.AlbumTabFragment;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.artist.ArtistTabFragment;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.music.MusicTabFragment;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.playlist.PlaylistTabFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private String[] tabTitle;
    private Fragment [] tabFragment;
    private int numTab;
    public ViewPagerAdapter(FragmentManager fm, int numTab, Context c) {
        super(fm);
        context = c;
        this.numTab = numTab;
        tabTitle = context.getResources().getStringArray(R.array.tab_title);
        tabFragment = new Fragment[numTab];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                MusicTabFragment musicTabFragment;
                if(tabFragment[0] ==null) {
                    musicTabFragment = new MusicTabFragment();
                    tabFragment[0] = musicTabFragment;
                }else{
                    musicTabFragment= (MusicTabFragment) tabFragment[0];
                }
                return musicTabFragment;
            }
            case 1:{
                AlbumTabFragment albumTabFragment;
                if(tabFragment[1] ==null) {
                    albumTabFragment = new AlbumTabFragment();
                    tabFragment[1] = albumTabFragment;
                }else{
                    albumTabFragment= (AlbumTabFragment) tabFragment[1];
                }
                return albumTabFragment;
            }
            case 2:{
                ArtistTabFragment artistTabFragment;
                if(tabFragment[2] == null) {
                    artistTabFragment = new ArtistTabFragment();
                    tabFragment[2] = artistTabFragment;
                }else{
                    artistTabFragment= (ArtistTabFragment) tabFragment[2];
                }
                return artistTabFragment;
            }
            case 3:{
                PlaylistTabFragment playlistTabFragment;
                if(tabFragment[3] == null) {
                    playlistTabFragment = new PlaylistTabFragment();
                    tabFragment[3] = playlistTabFragment;
                }else{
                    playlistTabFragment= (PlaylistTabFragment) tabFragment[3];
                }
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

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
