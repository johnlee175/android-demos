package com.version.increment.patch;

import java.io.File;
import java.io.IOException;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.sample_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread("test") {
                    @Override
                    public void run() {
                        File config = new File("/mnt/sdcard/abc.xyz");
                        if (config.exists()) {
                            try {
                                String data = FileUtils.readFileToString(config, FileUtils.UTF_8);
                                String[] items = data.split("&");
                                // call startService in application instread
                                startService(VersionService.getIntent(getApplicationContext(), items[0], items[1],
                                        items[2], items[3], items[4], Boolean.valueOf(items[5]),
                                        Boolean.valueOf(items[6]), Boolean.valueOf(items[7]),
                                        Boolean.valueOf(items[8])));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            config.delete();
                        }
                    }
                }.start();
            }
        });
        VersionService.showVersionNotification(this);
    }
}
