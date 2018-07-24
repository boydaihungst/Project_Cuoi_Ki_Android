package com.example.dragon.project_cuoi_ki_android.Controller;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.dragon.project_cuoi_ki_android.model.Song;

import java.util.ArrayList;
import java.util.Random;

public class PlayerService extends Service {

    public final static int loopMode_NO = 0;
    public final static int loopMode_SINGLE = 1;
    public final static int loopMode_ALL = 2;
    private final IBinder binder = new binderClass();
    private MediaPlayer mp;
    private ArrayList<Song> listSong = new ArrayList<>();
    private int currentSongPosition = -1;
    private ServiceReceiver serviceReceiver;
    private Handler handler = new Handler();
    private int loopMode;
    private boolean isShuffle;
    private boolean isSeeking;

    public PlayerService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.listSong = new ArrayList<>();
        this.mp = new MediaPlayer();
        serviceReceiver = new ServiceReceiver(this);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Bundle bundle = new Bundle();
                bundle.putInt(ServiceReceiver.PAUSE, ServiceReceiver.RESPONSE_OK);
                serviceReceiver.send(ServiceReceiver.PAUSE, bundle);
                if (isShuffle) {
                    Random r = new Random();
                    int index = 0;
                    if (listSong.size() > 1) {
                        index = r.nextInt(listSong.size() - 1);
                    } else {
                        return;
                    }
                    currentSongPosition = index;
                    playSong();
                    return;
                }
                switch (loopMode) {
                    case loopMode_ALL: {
                        if (currentSongPosition == (listSong.size() - 1)) {
                            currentSongPosition = 0;
                            playSong();
                            return;
                        }
                        break;
                    }
                    case loopMode_SINGLE: {
                        playSong();
                        return;
                    }
                }
                if (currentSongPosition < listSong.size() - 1 && currentSongPosition >= 0) {
                    ++currentSongPosition;
                    playSong();
                    return;
                }
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(ClientReceiver.CURRENT_POSITION);
        filter.addAction(ClientReceiver.PLAY);
        filter.addAction(ClientReceiver.PAUSE);
        filter.addAction(ClientReceiver.STOP);
        filter.addAction(ClientReceiver.SHUFFLE);
        filter.addAction(ClientReceiver.NEXT);
        filter.addAction(ClientReceiver.PREV);
        filter.addAction(ClientReceiver.APEEND_LIST_SONG);
        filter.addAction(ClientReceiver.DELETE_ONE_FROM_LIST_SONG);
        filter.addAction(ClientReceiver.DELETE_ALL_FROM_LIST_SONG);
        filter.addAction(ClientReceiver.SEEK);
        filter.addAction(ClientReceiver.RESUME);
        filter.addAction(ClientReceiver.LOOPING);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);


        registerReceiver(serviceReceiver, filter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class binderClass extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("onTaskRemoved", "service on onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendData);
        if(mp!=null) {
            mp.release();
        }
        listSong.clear();
        unregisterReceiver(serviceReceiver);
        super.onDestroy();
    }

    public void playSong() {
        if (mp.isPlaying()) {
            handler.removeCallbacks(sendData);
        }
        mp.reset();
        try {
            mp.setDataSource(listSong.get(currentSongPosition).getUrl());
            mp.prepare();
            mp.start();
            //gui tin hieu là chạy bài này
            // send play trigger
            Bundle bundle = new Bundle();
            bundle.putInt(ServiceReceiver.PLAY, currentSongPosition);
            serviceReceiver.send(ServiceReceiver.PLAY, bundle);
            //
            handler.post(sendData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Runnable sendData = new Runnable() {
        public void run() {
            try {
                if (!mp.isPlaying()) handler.removeCallbacks(sendData);
                // send thoi gian hien dang phat
                serviceReceiver.send(ServiceReceiver.CURRENT_POSITION,  mp.getCurrentPosition());
                //

                handler.postDelayed(this, 500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //func control media player
    public void Play(int _songId) {
        if (listSong.isEmpty()) {
            currentSongPosition = -1;
            return;
        } else
            currentSongPosition = 0;
        for (int i = 0; i < listSong.size(); i++) {
            if (listSong.get(i).getId() == _songId) {
                currentSongPosition = i;
                playSong();
                break;
            }
        }
    }

    public void Resume() {
        if (listSong.isEmpty()) {
            currentSongPosition = -1;
            return;
        } else if (currentSongPosition == -1) {
            currentSongPosition = 0;
            playSong();

        } else {
            mp.pause();
            mp.seekTo(mp.getCurrentPosition());
            mp.start();
            // send pause trigger
            Bundle bundle = new Bundle();
            bundle.putInt(ServiceReceiver.RESUME, ServiceReceiver.RESPONSE_OK);
            serviceReceiver.send(ServiceReceiver.RESUME, bundle);
            //
        }
    }

    public void Pause() {
        if (currentSongPosition < 0) return;
        if (mp.isPlaying()) {
            mp.pause();
        }
        // send pause trigger
        Bundle bundle = new Bundle();
        bundle.putInt(ServiceReceiver.PAUSE, ServiceReceiver.RESPONSE_OK);
        serviceReceiver.send(ServiceReceiver.PAUSE, bundle);
        //
    }

    public void Stop() {
        if (currentSongPosition < 0) return;
        if (mp.isPlaying()) {
            mp.stop();
        }
        // send stop trigger
        Bundle bundle = new Bundle();
        bundle.putInt(ServiceReceiver.STOP, ServiceReceiver.RESPONSE_OK);
        serviceReceiver.send(ServiceReceiver.STOP, bundle);
        //
    }

    public void Shuffle(boolean isShuffle) {
        this.isShuffle = isShuffle;
        // send stop trigger
        Bundle bundle = new Bundle();
        bundle.putBoolean(ServiceReceiver.SHUFFLE, isShuffle);
        serviceReceiver.send(ServiceReceiver.SHUFFLE, bundle);
        //
    }

    public void Seek(int position) {
        if (currentSongPosition < 0) return;
        mp.seekTo(position);
    }

    public void Next() {
        if (currentSongPosition < listSong.size() - 1) {
            if (mp.isPlaying())
                mp.stop();
            if (isShuffle) {
                Random r = new Random();
                int index = r.nextInt(listSong.size() - 1);
                currentSongPosition = index;
            } else {
                ++currentSongPosition;
            }
            playSong();

            // send next ok
            Bundle bundle = new Bundle();
            bundle.putInt(ServiceReceiver.NEXT, currentSongPosition);
            serviceReceiver.send(ServiceReceiver.NEXT, bundle);
        }
    }

    public void Prev() {
        if (currentSongPosition > 0) {
            if (mp.isPlaying())
                mp.stop();
            if (isShuffle) {
                Random r = new Random();
                int index = r.nextInt(listSong.size() - 1);
                currentSongPosition = index;
            } else {
                --currentSongPosition;
            }
            playSong();

            // response prev ok
            Bundle bundle = new Bundle();
            bundle.putInt(ServiceReceiver.PREV, currentSongPosition);
            serviceReceiver.send(ServiceReceiver.PREV, bundle);
            //
        }

    }

    public void Looping(int loopMode) {
        Bundle bundle = new Bundle();
        serviceReceiver.send(ServiceReceiver.LOOPING, bundle);
        this.loopMode = loopMode;
    }

    public void AppendListSong(Song _song) {
        if (!listSong.contains(_song)) {
            listSong.add(_song);
            // response Append ok
            Bundle bundle = new Bundle();
            bundle.putInt(ServiceReceiver.APEEND_LIST_SONG, _song.getId());
            serviceReceiver.send(ServiceReceiver.APEEND_LIST_SONG, bundle);
        } else {
            Log.d("Append error", "");
        }
    }

    public void DeleteOneListSong(int _songId) {
        int songNeedRemovePosition = listSong.indexOf(_songId);
        if (songNeedRemovePosition >= 0) {
            listSong.remove(songNeedRemovePosition);
        } else return;
        if (songNeedRemovePosition == currentSongPosition) {
            if (mp.isPlaying()) {
                Stop();
                currentSongPosition = 0;
                playSong();

            }
        } else if (songNeedRemovePosition >= 0 && songNeedRemovePosition < currentSongPosition) {
            --currentSongPosition;
        }
        // response delete ok
        serviceReceiver.send(ServiceReceiver.DELETE_ONE_FROM_LIST_SONG, _songId);
        //
    }

    public void DeleteAllListSong() {
        Stop();
        listSong.clear();
        currentSongPosition = -1;
        // response delete ok
        Bundle bundle = new Bundle();
        serviceReceiver.send(ServiceReceiver.DELETE_ALL_FROM_LIST_SONG, bundle);
        //
    }

}
