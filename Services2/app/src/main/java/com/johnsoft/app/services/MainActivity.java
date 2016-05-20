package com.johnsoft.app.services;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView)findViewById(R.id.content);
        if (textView != null) {
            String prefix;
            try {
                ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(),
                        PackageManager.GET_META_DATA);
                prefix = applicationInfo.metaData.getString("PF");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                prefix = "Unknown";
            }
            textView.setText(prefix + "\n" + Settings.CONTENT);
        }
    }
}
