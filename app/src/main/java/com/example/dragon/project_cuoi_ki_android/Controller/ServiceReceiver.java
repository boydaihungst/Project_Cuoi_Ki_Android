package com.example.dragon.project_cuoi_ki_android.Controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.model.Song;

public class ServiceReceiver extends BroadcastReceiver {
    private PlayerService context;
    private boolean isHeadsetOut;
    private boolean isHeadsetIn;

    SharedPreferences pref;
    //key
    public static final String CURRENT_SONG_ID = "com.dragon.action.CURRENT_SONG_ID";
    public static final String SONG = "com.dragon.action.SONG";
    public static final String CURRENT_POSITION = "com.dragon.action.CURRENT_POSITION";
    public static final int RESPONSE_OK = 200;
    public static final String STOP = "com.dragon.action.STOP";
    public static final String LOOPING = "com.dragon.action.LOOPING";
    public static final String SHUFFLE = "com.dragon.action.SHUFFLE";
    public static final String PLAY = "com.dragon.action.PLAY";
    public static final String NEXT = "com.dragon.action.NEXT";
    public static final String PREV = "com.dragon.action.PREV";
    public static final String PAUSE = "com.dragon.action.PAUSE";
    public static final String RESUME = "com.dragon.action.RESUME";
    public static final String APEEND_LIST_SONG = "com.dragon.action.APEEND_LIST_SONG";
    public static final String DELETE_ONE_FROM_LIST_SONG = "com.dragon.action.DELETE_ONE_FROM_LIST_SONG";
    public static final String DELETE_ALL_FROM_LIST_SONG = "com.dragon.action.DELETE_ALL_FROM_LIST_SONG";
    public ServiceReceiver(PlayerService context) {
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
        if (intent.getAction() != null)
            switch (intent.getAction()) {
                case ClientReceiver.STOP: {
                    this.context.Stop();
                    break;
                }
                case ClientReceiver.PLAY: {
                    int _songId = intent.getIntExtra(ClientReceiver.PLAY,-99);
                    this.context.Play(_songId);
                    break;
                }
                case ClientReceiver.NEXT: {
                    this.context.Next();
                    break;
                }
                case ClientReceiver.PREV: {
                    this.context.Prev();
                    break;
                }
                case ClientReceiver.SEEK: {
                    int position = bundle.getInt(ClientReceiver.SEEK);
                    this.context.Seek(position);
                    break;
                }
                case ClientReceiver.PAUSE: {
                    this.context.Pause();
                    break;
                }
                case ClientReceiver.RESUME: {
                    this.context.Resume();
                    break;
                }
                case ClientReceiver.APEEND_LIST_SONG: {
                    Song _song = bundle.getParcelable(ClientReceiver.APEEND_LIST_SONG);
                    this.context.AppendListSong(_song);
                    break;
                }
                case ClientReceiver.DELETE_ONE_FROM_LIST_SONG: {
                    int _songId = intent.getIntExtra(ClientReceiver.DELETE_ONE_FROM_LIST_SONG,-99);
                    this.context.DeleteOneListSong(_songId);
                    break;
                }
                case ClientReceiver.DELETE_ALL_FROM_LIST_SONG: {
                    this.context.DeleteAllListSong();
                    break;
                }
                case ClientReceiver.SHUFFLE: {
                    boolean isShuffle = intent.getBooleanExtra(ClientReceiver.SHUFFLE,false);
                    this.context.Shuffle(isShuffle);
                    break;
                }
                case ClientReceiver.LOOPING: {
                    //0-khong loop
                    //1-loop single
                    //2-loop all
                    int loopMode = intent.getIntExtra(ClientReceiver.LOOPING,0);
                    this.context.Looping(loopMode);
                    break;
                }
                case Intent.ACTION_HEADSET_PLUG: {
                    int state = intent.getIntExtra("state", -1);
                    String headphoneOut = context.getString(R.string.pref_key_Headset_Out);
                    String headphoneIn = context.getString(R.string.pref_key_Headset_In);
                    pref = context.getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
                    isHeadsetOut=pref.getBoolean(headphoneOut, false);
                    isHeadsetIn=pref.getBoolean(headphoneIn, false);
                    switch (state) {
                        case 0:
                            Toast.makeText(context, "Headset unplugged", Toast.LENGTH_SHORT).show();
//                            Log.d(TAG, "Headset unplugged");
                            if (isHeadsetOut) {
                                this.context.Pause();
                            }
                            break;
                        case 1:
                            Toast.makeText(context, "Headset plugged", Toast.LENGTH_SHORT).show();
//                            Log.d(TAG, "Headset plugged");
                            if (isHeadsetIn) {
                                this.context.Resume();
                            }
                            break;
                    }
                    break;
                }
            }
    }
}
