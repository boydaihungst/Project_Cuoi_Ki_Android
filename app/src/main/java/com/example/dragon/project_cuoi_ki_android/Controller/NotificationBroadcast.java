package com.example.dragon.project_cuoi_ki_android.Controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcast extends BroadcastReceiver {
    private NotificationMaker context;
    public NotificationBroadcast(NotificationMaker context) {
        this.context= context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()){
            case ServiceReceiver.PLAY :{
                this.context.showNotification(context);

                break;
            }
            case ServiceReceiver.PAUSE :{
                this.context.showNotification(context);
                break;
            }
        }
    }
}
