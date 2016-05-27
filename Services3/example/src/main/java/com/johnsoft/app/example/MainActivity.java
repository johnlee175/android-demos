package com.johnsoft.app.example;

import com.johnsoft.app.clientsdk.ServicesManager;
import com.johnsoft.app.clientsdk.proxys.ChannelManager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread("Test-Thread") {
            @Override
            public void run() {
                ServicesManager.initServicesManager(getApplicationContext());
                ServicesManager.waitForServicesReady();
                ChannelManager channelManager = ServicesManager.getChannelManager();
                System.err.println(">>>>>>>>>>>>>>>>>>>>" + channelManager.description());
            }
        }.start();
    }
}
