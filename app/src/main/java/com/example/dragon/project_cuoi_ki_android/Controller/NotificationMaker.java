package com.example.dragon.project_cuoi_ki_android.Controller;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.dragon.project_cuoi_ki_android.MainActivity;
import com.example.dragon.project_cuoi_ki_android.R;
import com.example.dragon.project_cuoi_ki_android.model.Song;

public class NotificationMaker {
    private static final int NOTIF_ID = 1234;
    private NotificationCompat.Builder nc;
    private NotificationManager nm;
    private RemoteViews remoteViews;
    private Intent play;
    private  PendingIntent pPlay;
    @SuppressLint("RestrictedApi")
    public void showNotification(Context context){
        if(nc == null) {
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
            nc = new NotificationCompat.Builder(context);
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notifyIntent = new Intent(context, MainActivity.class);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            nc.setContentIntent(pendingIntent);
            nc.setSmallIcon(R.drawable.ic_audiotrack_black_24dp);
            nc.setAutoCancel(false);
            nc.setCustomContentView(remoteViews);
            nc.setContentTitle("Music player");
            nc.setContentText("Music player");
            nc.setPriority(Notification.PRIORITY_MAX);
            nc.setOngoing(true);
            nc.getContentView().setTextViewText(R.id.noti_tvSongName, "Ten bai hat");
            nc.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle());
            setListener(remoteViews, context);
        }
        nm.notify(NOTIF_ID, nc.build());
    }
    private void setListener(RemoteViews view ,Context context){
        Intent next = new Intent(ClientReceiver.NEXT);
        Intent prev = new Intent(ClientReceiver.PREV);
        play = new Intent(ClientReceiver.RESUME);

        PendingIntent pNext = PendingIntent.getBroadcast(context,0,next,PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.noti_next,pNext);

        PendingIntent pPrev = PendingIntent.getBroadcast(context,0,prev,PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.noti_prev,pPrev);

        pPlay = PendingIntent.getBroadcast(context,0,play,PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.noti_play,pPlay);

    }
    public void updateNotification(Context context,Song song,boolean isPlayBtn){
        if(song !=null) {
            // update the icon
            if(song.getPicture() !=null) {
                remoteViews.setImageViewBitmap(R.id.noti_picture, song.getPicture());
            }else{
                remoteViews.setImageViewResource(R.id.noti_picture, R.drawable.ic_audiotrack_black_24dp);
            }
            // update the title
            remoteViews.setTextViewText(R.id.noti_tvSongName, song.getTitle());
            remoteViews.setTextViewText(R.id.noti_tvArtistName, song.getArtist());
        }
        if(isPlayBtn){
            nc.setOngoing(true);
            nc.setAutoCancel(false);
            remoteViews.setImageViewResource(R.id.noti_play,android.R.drawable.ic_media_pause);
            play.setAction(ClientReceiver.PAUSE);
            pPlay.cancel();
            pPlay = PendingIntent.getBroadcast(context,0,play,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.noti_play,pPlay);
        }else{
            nc.setOngoing(false);
            nc.setAutoCancel(true);
            remoteViews.setImageViewResource(R.id.noti_play,android.R.drawable.ic_media_play);
            play.setAction(ClientReceiver.RESUME);
            pPlay.cancel();
            pPlay = PendingIntent.getBroadcast(context,0,play,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.noti_play,pPlay);
        }
        // update the notification
            nm.notify(NOTIF_ID, nc.build());
    }
    public void turnOffNoti(){
        if(nc!=null && nm!=null) {
            nc.setOngoing(false);
            nc.setAutoCancel(true);
            nm.cancel(NOTIF_ID);
        }
    }
}
