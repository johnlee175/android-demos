package com.johnsoft.app.services;

import com.johnsoft.app.services.services.ChannelService;
import com.johnsoft.app.services.services.ServicesManagerNative;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class ServicesManagerService extends Service {
    private static final boolean FOREGROUND_SERVICE = true;
    @Override
    public void onCreate() {
        super.onCreate();
        ChannelService.main();
        if (FOREGROUND_SERVICE) {
            startForeground("ServicesManagerService".hashCode(), createNotification());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return ServicesManagerNative.getDefault();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (FOREGROUND_SERVICE) {
            stopForeground(true);
        }
    }

    private Notification createNotification() {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(false);
        builder.setWhen(System.currentTimeMillis());
        builder.setShowWhen(true);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Listener-Service");
        builder.setContentText("com.johnsoft.app.services");
        builder.setContentIntent(PendingIntent.getActivity(this, "ServicesManagerService".hashCode(),
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        return builder.build();
    }
}
