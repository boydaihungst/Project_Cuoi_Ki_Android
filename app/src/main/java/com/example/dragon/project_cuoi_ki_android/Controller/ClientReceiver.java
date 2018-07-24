package com.example.dragon.project_cuoi_ki_android.Controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.dragon.project_cuoi_ki_android.MainActivity;
import com.example.dragon.project_cuoi_ki_android.model.Song;

import java.util.ArrayList;

public class ClientReceiver extends BroadcastReceiver {
    private MainActivity context;

    public static final String CURRENT_SONG_ID = "com.dragon.action.CURRENT_SONG_ID2";
    public static final String SONG = "com.dragon.action.SONG2";
    public static final String CURRENT_POSITION = "com.dragon.action.CURRENT_POSITION2";
    public static final int RESPONSE_OK = 200;
    public static final String STOP = "com.dragon.action.STOP2";
    public static final String RESUME = "com.dragon.action.RESUME2";
    public static final String SHUFFLE = "com.dragon.action.SHUFFLE2";
    public static final String SEEK = "com.dragon.action.SEEK2";
    public static final String PLAY = "com.dragon.action.PLAY2";
    public static final String NEXT = "com.dragon.action.NEXT2";
    public static final String PREV = "com.dragon.action.PREV2";
    public static final String PAUSE = "com.dragon.action.PAUSE2";
    public static final String LOOPING = "com.dragon.action.LOOPING2";
    public static final String APEEND_LIST_SONG = "com.dragon.action.APEEND_LIST_SONG2";
    public static final String DELETE_ONE_FROM_LIST_SONG = "com.dragon.action.DELETE_ONE_FROM_LIST_SONG2";
    public static final String DELETE_ALL_FROM_LIST_SONG = "com.dragon.action.DELETE_ALL_FROM_LIST_SONG2";
    public static final String SETTING_CHANGED = "com.dragon.action.SETTING_CHANGED";
    //setting change action
    public static final String SETTING_CHANGED_HEADSET_OUT = "com.dragon.action.SETTING_CHANGED_HEADSET_OUT";
    public static final String SETTING_CHANGED_HEADSET_IN = "com.dragon.action.SETTING_CHANGED_HEADSET_IN";

    public ClientReceiver(MainActivity context) {
        this.context = context;
    }

    public void send(String action, Bundle bundle) {
        Intent i = new Intent();
        i.setAction(action);
        i.putExtras(bundle);
        context.sendBroadcast(i);
    }
    public void send(String action,int value) {
        Intent i = new Intent();
        i.setAction(action);
        i.putExtra(action, value);
        context.sendBroadcast(i);
    }
    public void send(String action, boolean value) {
        Intent i = new Intent();
        i.setAction(action);
        i.putExtra(action,value);
        context.sendBroadcast(i);
    }
    public void send(String action) {
        Intent i = new Intent();
        i.setAction(action);
        context.sendBroadcast(i);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ServiceReceiver.STOP: {
                    this.context.responseStop();
                    break;
                }
                case ServiceReceiver.PLAY: {
                    int currentSongPosition = bundle.getInt(ServiceReceiver.PLAY);
                    this.context.responsePlay(this.context.getListSong().get(currentSongPosition));
                    break;
                }
                case ServiceReceiver.NEXT: {
                    this.context.responseNext();
                    break;
                }
                case ServiceReceiver.PREV: {
                    this.context.responsePrev();
                    break;
                }
                case ServiceReceiver.PAUSE: {
                    this.context.responsePause();
                    break;
                }
                case ServiceReceiver.RESUME: {
                    this.context.responseResume();
                    break;
                }
                case ServiceReceiver.LOOPING: {
                    this.context.responseLoop();
                    break;
                }
                case ServiceReceiver.APEEND_LIST_SONG: {
                    int songId = bundle.getInt(ServiceReceiver.APEEND_LIST_SONG);
                    this.context.responseAppendList(songId);
                    break;
                }
                case ServiceReceiver.SHUFFLE: {
                    boolean isShuffle = bundle.getBoolean(ServiceReceiver.SHUFFLE);
                    this.context.responseShuffle(isShuffle);
                    break;
                }
                case ServiceReceiver.DELETE_ONE_FROM_LIST_SONG: {
                    int songId = intent.getIntExtra(ServiceReceiver.DELETE_ONE_FROM_LIST_SONG,-99);
                    Song temp = new Song();
                    temp.setId(songId);
                    this.context.responseDeleteOneFromList(temp);
                    break;
                }
                case ServiceReceiver.DELETE_ALL_FROM_LIST_SONG: {
                    this.context.responseDeleteAllFromList();
                    break;
                }
                case ServiceReceiver.CURRENT_POSITION: {
                    int currentPosition = intent.getIntExtra(ServiceReceiver.CURRENT_POSITION,0);
                    this.context.updateProgress(currentPosition);
                    break;
                }
                case FragmentBroadcast.ADD_SONG_NOW_PLAYING: {
                    Song song = bundle.getParcelable(FragmentBroadcast.ADD_SONG_NOW_PLAYING);
                    this.context.requestAppendList(song);
                    break;
                }
                case FragmentBroadcast.ADD_ALL_SONG_NOW_PLAYING: {
                    this.context.requestSongDeleteAll();
                    ArrayList<Song> listSong = bundle.getParcelableArrayList(FragmentBroadcast.ADD_ALL_SONG_NOW_PLAYING);
                    this.context.requestAppendListAndPlay(listSong);
                    break;
                }
                case FragmentBroadcast.DELETE_SONG_IN_DB: {
                    Song song = bundle.getParcelable(FragmentBroadcast.DELETE_SONG_IN_DB);
                    this.context.songDeletedInDB(song);
                    break;
                }
                case FragmentBroadcast.PLAYLIST_CHANGED: {
                    this.context.responseUpdatePlaylistTab();
                    break;
                }
            }
        }
    }
}
