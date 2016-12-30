/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package com.version.increment.patch;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-30
 */
public class VersionAboutActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        final TextView currentVersionInfo = (TextView) findViewById(R.id.current_version_info);
        final TextView versionDetailList = (TextView) findViewById(R.id.version_detail_list);
        final Button versionSwitch = (Button)findViewById(R.id.check_version_update);
        if (currentVersionInfo != null) {
            currentVersionInfo.setText(VersionManager.getVersionName(this));
        }
        if (versionDetailList != null) {
            versionDetailList.setText("此版本:\n"
                    + "修复了一些莫名其妙的bug;\n"
                    + "将一些很好用的功能故意改坏又改回来, 以增加GDP;\n");
        }
        if (versionSwitch != null) {
            versionSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgress("温馨提示", "请稍等...");
                    SimpleTaskExecutor.scheduleNow(new SimpleTaskExecutor.AbstractSafeTask() {
                        @Override
                        public void doTask() {
                            doTaskWhenFitNetworkConnected(new InetSocketAddress("http://build-server.com/", 80),
                                    COMPLEX,
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            showSelectDialog("版本选择", "飞跃未来还是回到过去, 尽在你手!",
                                                    new String[] {"A. 更新到最新版本", "B. 还原到上一版本"},
                                                    new OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(String item, int position) {
                                                            boolean upToDate;
                                                            boolean forwardStep;
                                                            if (position == 1 && item.startsWith("B.")) {
                                                                upToDate = false;
                                                                forwardStep = false;
                                                            } else {
                                                                upToDate = true;
                                                                forwardStep = true;
                                                            }

                                                            final VersionManager.VersionParts parts =
                                                                    VersionManager.getVersionParts(
                                                                            VersionAboutActivity.this);

                                                            startService(VersionService.getIntent(
                                                                    VersionAboutActivity.this,
                                                                    parts.product, parts.branch, parts.flavor,
                                                                    parts.strDateTime.split("-")[0], parts.mappingCode,
                                                                    upToDate, forwardStep, false, false));
                                                        }
                                                    });
                                        }
                                    });
                        }
                        @Override
                        public void onThrowable(Throwable t) {
                            Log.w("VersionAboutActivity", t);
                        }
                        @Override
                        public String getName() {
                            return "VersionAboutActivityUpdate";
                        }
                    });
                }
            });
        }
    }

    private void showSelectDialog(String title, String message, String[] items,
                                  final OnItemSelectedListener listener) {
        final DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    try {
                        if (listener != null) {
                            final ListView listView = ((AlertDialog) dialog).getListView();
                            int position = listView.getCheckedItemPosition();
                            String item = (String) listView.getAdapter().getItem(position);
                            listener.onItemSelected(item, position);
                        }
                    } finally {
                        dialog.dismiss();
                    }
                } else {
                    dialog.cancel();
                }
            }
        };
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,
                R.style.AppAlertDialogTheme);
        alertDialogBuilder.setTitle(title + " - " + message)
                .setSingleChoiceItems(items, 0, null)
                .setPositiveButton("OK", l)
                .setNegativeButton("CANCEL", l);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        final Window window = alertDialog.getWindow();
        if (window != null) {
            window.setType(WindowManager.LayoutParams.TYPE_TOAST);
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.show();
            }
        });
    }

    public interface OnItemSelectedListener {
        void onItemSelected(String item, int position);
    }

    public static final int TOAST_TIP = 1;
    public static final int STATUS_BAR = 2;
    public static final int GOTO_SETTINGS = 3;
    public static final int GOTO_WIFI_SETTINGS = 4;
    public static final int GOTO_MOBILE_SETTINGS = 5;
    public static final int COMPLEX = 6;

    /**
     *
     * @param address test server address;
     * @param handleNoNet if no network, which way to handle it, see:
     *                    {@link VersionAboutActivity#TOAST_TIP},
     *                    {@link VersionAboutActivity#STATUS_BAR},
     *                    {@link VersionAboutActivity#GOTO_SETTINGS},
     *                    {@link VersionAboutActivity#GOTO_WIFI_SETTINGS},
     *                    {@link VersionAboutActivity#GOTO_MOBILE_SETTINGS},
     *                    {@link VersionAboutActivity#COMPLEX};
     * @param task if all condition match, which task will be do;
     */
    @WorkerThread
    private void doTaskWhenFitNetworkConnected(final InetSocketAddress address, final int handleNoNet,
                                               final Runnable task) {
        final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isAvailable() && netInfo.isConnected()) {
            Socket socket = new Socket();
            boolean reachable = false;
            try {
                socket.connect(address, 5000);
                reachable = true;
            } catch (IOException ignored) {
                reachable = false;
            } finally {
                try {
                    socket.close();
                } catch (Exception e) {
                    // close silently
                }
            }
            if (reachable) {
                final int netInfoType = netInfo.getType();
                if (netInfoType == ConnectivityManager.TYPE_WIFI) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgress(false);
                            task.run();
                        }
                    });
                } else if (netInfoType == ConnectivityManager.TYPE_MOBILE) {
                    final DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                try {
                                    task.run(); // this is  main thread
                                } finally {
                                    dialog.dismiss();
                                }
                            } else {
                                dialog.cancel();
                            }
                        }
                    };
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,
                            R.style.AppAlertDialogTheme);
                    alertDialogBuilder.setTitle("温馨提示")
                            .setMessage("当前的网络环境不是WIFI, 可能产生流量资费, 要继续操作吗")
                            .setPositiveButton("继续", l)
                            .setNegativeButton("取消", l);
                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    final Window window = alertDialog.getWindow();
                    if (window != null) {
                        window.setType(WindowManager.LayoutParams.TYPE_TOAST);
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgress(false);
                            alertDialog.show();
                        }
                    });
                } else {
                    Log.w("VersionAboutActivity", "doTaskWhenFitNetworkConnected: Unknown connectivity type, "
                            + "do nothing, task had iscard!");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgress(false);
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress(false);
                        Toast.makeText(VersionAboutActivity.this, "目标服务器无法访问 请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress(false);
                    handleNoNet(handleNoNet);
                }
            });
        }
    }

    @MainThread
    private void handleNoNet(int handleNoNet) {
        switch (handleNoNet) {
            case TOAST_TIP: {
                Toast.makeText(this, "请检查网络是否已连接", Toast.LENGTH_SHORT).show();
            }
                break;
            case GOTO_SETTINGS: {
                Toast.makeText(this, "没有发现可用网络, 将为您跳转到设置界面", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
                break;
            case GOTO_WIFI_SETTINGS: {
                Toast.makeText(this, "没有发现可用网络, 将为您跳转到设置界面", Toast.LENGTH_SHORT).show();
                Intent intent =  new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            }
                break;
            case GOTO_MOBILE_SETTINGS: {
                Toast.makeText(this, "没有发现可用网络, 将为您跳转到设置界面", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                ComponentName cName = new ComponentName("com.android.settings",
                        "com.android.settings.Settings$DataUsageSummaryActivity");
                intent.setComponent(cName);
                startActivity(intent);
            }
                break;
            case STATUS_BAR: {
                Toast.makeText(this, "没有发现可用网络, 将为您跳转到设置界面", Toast.LENGTH_SHORT).show();
                autoDropDownStatusBar();
            }
                break;
            case COMPLEX: {
                final DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            try {
                                if (!autoDropDownStatusBar()) {
                                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                    startActivity(intent);
                                }
                            } finally {
                                dialog.dismiss();
                            }
                        } else {
                            dialog.cancel();
                        }
                    }
                };
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,
                        R.style.AppAlertDialogTheme);
                alertDialogBuilder.setTitle("温馨提示")
                        .setMessage("没有发现可用网络, 是否为您跳转到设置界面?")
                        .setPositiveButton("好的", l)
                        .setNegativeButton("算了", l);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                final Window window = alertDialog.getWindow();
                if (window != null) {
                    window.setType(WindowManager.LayoutParams.TYPE_TOAST);
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                }
                alertDialog.show();
            }
                break;
        }
    }

    // need <uses-permission android:name="android.permission.EXPAND_STATUS_BAR" />
    @SuppressWarnings("WrongConstant")
    @MainThread
    private boolean autoDropDownStatusBar() {
        final Object statusBarManager = getSystemService("statusbar");
        if (statusBarManager != null) {
            try {
                final Class<?> statusbarManagerClass = statusBarManager.getClass();
                final Method method = statusbarManagerClass.getDeclaredMethod("expandSettingsPanel");
                if (method != null) {
                    method.setAccessible(true);
                    method.invoke(statusBarManager);
                    return true;
                }
            } catch (Exception e) {
                Log.w("", e);
            }
        }
        return false;
    }

    private void showProgress(String title, String message) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
            return;
        }
        progressDialog = ProgressDialog.show(this, title, message, true, false);
    }

    private void hideProgress(boolean justHide) {
        if (progressDialog != null) {
            if (justHide) {
                progressDialog.hide();
            } else {
                progressDialog.dismiss();
            }
        }
    }
}

