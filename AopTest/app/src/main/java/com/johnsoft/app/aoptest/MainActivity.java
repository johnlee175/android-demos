package com.johnsoft.app.aoptest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

/**
 * @author John Kenrinus Lee
 * @version 2016-06-19
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("Activity", "begin onCreate(Bundle)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.w("Activity", "end onCreate(Bundle)");
    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpUtils.printHttpRequestResultAsync("http://www.baidu.com");
        HttpUtils.printHttpClientASync(this, "http://www.163.com");
    }
}
