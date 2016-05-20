package com.johnsoft.app.example;

import com.johnsoft.app.clientsdk.ServicesManager;

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
                Bundle bundle = new Bundle();
                try {
                    int resultCode = ServicesManager.request(ServicesManager.URL_CHANNEL + "/description", bundle);
                    System.err.println("Sync>>>>>>>>>>>>>>>>>>>>" + resultCode + ", " + bundle.getString("result"));
                    ServicesManager.asyncRequest(ServicesManager.URL_VERBAL + "/description", bundle,
                            new ServicesManager.OnServiceResponse() {
                                @Override
                                public void onResponse(int resultCode, Bundle bundle) {
                                    System.err.println("ASync>>>>>>>>>>>>>>>>>>>>"
                                            + resultCode + ", " + bundle.getString("result"));
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
