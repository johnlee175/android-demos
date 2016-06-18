package com.johnsoft.app.aoptest2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("Activity", "begin onCreate(Bundle)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.w("Activity", "end onCreate(Bundle)");
    }
}
