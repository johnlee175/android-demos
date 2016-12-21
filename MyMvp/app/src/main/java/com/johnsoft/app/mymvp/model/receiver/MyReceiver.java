package com.johnsoft.app.mymvp.model.receiver;

import org.rxbus.RxBus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public class MyReceiver extends BroadcastReceiver {
    public static final int CONNECTIVITY_EVENT = 1001;
    public static final int PUSH_EVENT = 1002;
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            handleConnectivity(context);
        } else if (action.equals("action.pusher.message.com")) {
            handlePushMessage(intent.getStringExtra("msg"));
        }
    }

    private void handleConnectivity(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable() && info.isConnected()) {
            RxBus.singleInstance.postAsync(CONNECTIVITY_EVENT, info.getType());
        } else {
            RxBus.singleInstance.postAsync(CONNECTIVITY_EVENT, -1);
        }
    }

    private void handlePushMessage(String msg) {
        RxBus.singleInstance.postAsync(PUSH_EVENT, msg);
    }
}
