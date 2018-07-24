package com.example.dragon.project_cuoi_ki_android.Controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.dragon.project_cuoi_ki_android.MainActivity;

public class FragmentBroadcast extends BroadcastReceiver {
    public static final String ADD_SONG_NOW_PLAYING = "com.dragon.action.ADD_SONG_NOW_PLAYING";

    public static final String ADD_ALL_SONG_NOW_PLAYING = "com.dragon.action.ADD_ALL_SONG_NOW_PLAYING";
    public static final String DELETE_SONG_IN_DB = "com.dragon.action.DELETE_SONG_IN_DB";

    public static final String PLAYLIST_CHANGED = "com.dragon.action.PLAYLIST_CHANGED";
    Context context;

    public FragmentBroadcast(Context context) {
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
    }
}
