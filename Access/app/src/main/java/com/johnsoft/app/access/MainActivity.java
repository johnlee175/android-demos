package com.johnsoft.app.access;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button center = (Button)findViewById(R.id.center);
        if (center != null) {
            center.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                }
            });
        }
        Button left = (Button)findViewById(R.id.left);
        if (left != null) {
            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                        System.out.println("Will anr");
                        try {
                            Thread.sleep(10000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    }
                }
            });
        }
        Button top = (Button)findViewById(R.id.top);
        if (top != null) {
            top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Will kill TargetInstallable App");
                    final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    final Uri uri = Uri.parse("package:com.johnsoft.app.targetinstallable");
                    intent.setData(uri);
                    startActivity(intent);
                }
            });
        }
        Button right = (Button)findViewById(R.id.right);
        if (right != null) {
            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Will Uninstall TargetInstallable App");
                    final Intent intent = new Intent(Intent.ACTION_DELETE);
                    final Uri uri = Uri.parse("package:com.johnsoft.app.targetinstallable");
                    intent.setData(uri);
                    startActivity(intent);
                }
            });
        }
        Button bottom = (Button)findViewById(R.id.bottom);
        if (bottom != null) {
            bottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Will install TargetInstallable App");
                    final File installApk = new File(Environment.getExternalStorageDirectory(),
                            "com.johnsoft.app.targetinstallable.apk");
                    if(!installApk.exists()){
                        InputStream input = null;
                        OutputStream output = null;
                        try {
                            input = new BufferedInputStream(MainActivity.this.getAssets()
                                    .open("targetinstallable-debug.apk"));
                            output = new BufferedOutputStream(new FileOutputStream(installApk));
                            final byte[] buffer = new byte[4096];
                            int length = buffer.length, size = -1;
                            while ((size = input.read(buffer, 0, length)) > 0) {
                                output.write(buffer, 0, size);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (output != null) {
                                try {
                                    output.close();
                                } catch (IOException ignored) {
                                    // DO NOTHING
                                }
                            }
                            if (input != null) {
                                try {
                                    input.close();
                                } catch (IOException ignored) {
                                    // DO NOTHING
                                }
                            }
                        }
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(installApk), "application/vnd.android.package-archive");
                    startActivity(intent);
                }
            });
        }
    }

}
