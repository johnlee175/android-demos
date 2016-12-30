package com.version.increment.patch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-30
 */
public class ConnectivityReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isAvailable() && netInfo.isConnected()) {
                VersionService.showVersionNotification(context);
            }
        }
    }
}