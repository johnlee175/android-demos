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

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;

/**
 * @author John Kenrinus Lee
 * @version 2016-11-24
 */
public class VersionService extends IntentService {
    private static final String LOG_TAG = "VersionService";

    private static final int SHOW_UPDATE_DIALOG = 0;
    private static final int SHOW_PROGRESS = 1;
    private static final int HIDE_PROGRESS = 2;
    private static final int SHOW_TOAST = 4;
    private static final String KEY_MESSAGE = "message";

    public static Intent getIntent(Context context, String product, String branch, String flavor, String buildDate,
                                   String mappingCode, boolean upToDate, boolean forwardStep, boolean preparePatch,
                                   boolean daemon) {
        final Intent intent = new Intent(context, VersionService.class);
        intent.putExtra("product", product);
        intent.putExtra("branch", branch);
        intent.putExtra("flavor", flavor);
        intent.putExtra("buildDate", buildDate);
        intent.putExtra("mappingCode", mappingCode);
        intent.putExtra("upToDate", upToDate);
        intent.putExtra("forwardStep", forwardStep);
        intent.putExtra("preparePatch", preparePatch);
        intent.putExtra("daemon", daemon);
        return intent;
    }

    public static void showVersionNotification(Context appContext) {
        final SharedPreferences sp = getBackupStorage(appContext);
        final String peekDataJson = sp.getString("PeekData", "");
        final String fetchDataJson = sp.getString("FetchData", "");
        final String patchMd5 = sp.getString("PatchMd5", "");
        if (!peekDataJson.trim().isEmpty()
                && !fetchDataJson.trim().isEmpty()
                && !patchMd5.trim().isEmpty()) {
            appContext = appContext.getApplicationContext();
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext);
            builder.setAutoCancel(true);
            builder.setWhen(System.currentTimeMillis());
            builder.setShowWhen(true);
            builder.setOnlyAlertOnce(true);
            builder.setOngoing(false);
            builder.setTicker("有版本可以更新啦, 是否又蠢蠢欲动了");
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("戳我进行版本更新");
            builder.setContentText("有版本可以更新啦, 是否又蠢蠢欲动了");
            builder.setContentIntent(PendingIntent.getActivity(appContext, "VersionService".hashCode(),
                    new Intent(appContext, VersionAboutActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
            final Notification notification = builder.build();
            final NotificationManager notificationManager = (NotificationManager)appContext
                    .getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify("VersionService".hashCode(), notification);
        }
    }

    private static SharedPreferences getBackupStorage(Context context) {
        return context.getSharedPreferences("version_service", Context.MODE_PRIVATE);
    }

    private static void saveData(SharedPreferences sp, String peekData, String fetchData, String patchMd5) {
        sp.edit().putString("PeekData", peekData)
                .putString("FetchData", fetchData)
                .putString("PatchMd5", patchMd5).apply();
    }

    private final MainHandler mainHandler;
    private final Gson gson;
    private final OkHttpClient httpClient;
    private boolean isEveryThingOk;

    public VersionService() {
        super(LOG_TAG);
        mainHandler = new MainHandler(VersionService.this);
        gson = new GsonBuilder().enableComplexMapKeySerialization().setLenient().create();
        httpClient = new OkHttpClient.Builder().connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES).writeTimeout(2, TimeUnit.MINUTES)
                .addInterceptor(new GzipRequestInterceptor()).addInterceptor(new HttpLoggingInterceptor()).build();
        isEveryThingOk = true;
        VersionManager.initWith(VersionService.this, httpClient, gson,
                "http://build-server.com/");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            log("onHandleIntent begin time:" + System.currentTimeMillis());
            final File oldApk = new File(getPackageManager().getApplicationInfo(getPackageName(), 0).sourceDir);
            final Version.PeekData peekData = new Version.PeekData();
            peekData.product = intent.getStringExtra("product");
            peekData.branch = intent.getStringExtra("branch");
            peekData.flavor = intent.getStringExtra("flavor");
            peekData.buildDate = intent.getStringExtra("buildDate");
            peekData.mappingCode = intent.getStringExtra("mappingCode");
            peekData.upToDate = intent.getBooleanExtra("upToDate", true);
            peekData.forwardStep = intent.getBooleanExtra("forwardStep", true);
            peekData.preparePatch = intent.getBooleanExtra("preparePatch", true);
            peekData.apkMd5 = VersionManager.md5(oldApk);
            peekData.device = VersionManager.getAndroidDeviceInfo();
            peekData.deviceVersion = VersionManager.getAndroidVersionInfo();
            final boolean daemon = intent.getBooleanExtra("daemon", true);
            final boolean startAfterInstall = false; // the app install finish view has open button
            final int startDelayAfterInstalling = intent.getIntExtra("startDelayAfterInstalling", 5000);
            final SharedPreferences sp = getBackupStorage(VersionService.this);
            final String peekDataJson = sp.getString("PeekData", "");
            final String fetchDataJson = sp.getString("FetchData", "");
            final String spPatchMd5 = sp.getString("PatchMd5", "");
            boolean sameRequest = false;
            if (!peekDataJson.isEmpty()) {
                Version.PeekData spPeekData = gson.fromJson(peekDataJson, Version.PeekData.class);
                if (peekData.equals(spPeekData) && !spPatchMd5.isEmpty()) {
                    sameRequest = true;
                }
            }
            if (sameRequest) {
                peekData.preparePatch = false; // just check
            } else {
                saveData(sp, "", "", "");
            }
            final Version.ResponseResult responseResult = VersionManager.peekVersionInfo(peekData);
            log("peekVersionInfo: " + responseResult);
            if (responseResult != null) {
                if (responseResult.code == 0) {
                    if (responseResult.current.equals(responseResult.target)) {
                        log("Had got the target version");
                        if (!daemon) {
                            showToast("哦噢 没有对应版本了");
                        }
                        return;
                    }

                    final Version.PathMd5 from = responseResult.current;
                    final Version.PathMd5 to = responseResult.target;
                    final Version.FetchData fetchData = new Version.FetchData(from, to);
                    final File patch = new File(Environment.getExternalStorageDirectory(),
                            to.md5 + ".patch");

                    boolean handled = false;
                    if (sameRequest && !fetchDataJson.isEmpty() && patch.exists()
                            && patch.isFile() && patch.canRead()) {
                        Version.FetchData spFetchData = gson.fromJson(fetchDataJson, Version.FetchData.class);
                        if (fetchData.equals(spFetchData) && spPatchMd5.equals(VersionManager.md5(patch))) {
                            handled = true;
                            showAskUpdateDialogAfterHadPatch(oldApk, peekData, fetchData, patch, spPatchMd5,
                                    responseResult.patchSize, startAfterInstall, startDelayAfterInstalling);
                        }
                    }
                    if (!handled) {
                        saveData(sp, "", "", "");
                        if (!daemon) {
                            final DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == DialogInterface.BUTTON_POSITIVE) {
                                        showProgress("请稍后, 正在下载并安装补丁...");
                                        new Thread("Apply-Patch-Thread") {
                                            @Override
                                            public void run() {
                                                final String[] md5s = VersionManager.fetchPatch(fetchData, patch);
                                                log("fetchPatch: successful!!!");
                                                if (patch.exists() && (md5s != null && md5s.length == 2
                                                                               && md5s[1] != null
                                                                               && !md5s[1].trim().isEmpty()
                                                                               && md5s[1].equals(to.md5))) {
                                                    doUpdate(oldApk, patch, from, to,
                                                            startAfterInstall, startDelayAfterInstalling);
                                                } else {
                                                    showToast("下载更新补丁失败");
                                                    hideProgress();
                                                }
                                            }
                                        }.start();
                                    } else {
                                        isEveryThingOk = true;
                                    }
                                    dialog.dismiss();
                                }
                            };

                            showAskUpdateDialog(responseResult.patchSize, from, to, l);
                        } else {
                            final String[] md5s = VersionManager.fetchPatch(fetchData, patch);
                            if (patch.exists() && (md5s != null && md5s.length == 2 && md5s[1] != null
                                                           && !md5s[1].trim().isEmpty() && md5s[1].equals(to.md5))) {
                                log("fetchPatch: successful");
                                showAskUpdateDialogAfterHadPatch(oldApk, peekData, fetchData, patch, md5s[0],
                                        responseResult.patchSize, startAfterInstall, startDelayAfterInstalling);
                            }
                        }
                    }
                } else if (!daemon) {
                    showToast("哦噢 版本查询请求失败了");
                }
            } else if (!daemon) {
                showToast("哦噢 好像没有对应版本了");
            }
        } catch (Exception e) {
            log(e);
        }
        try {
            synchronized(VersionService.this) {
                int timeoutCount = 300; // 5 minutes
                while (!isEveryThingOk) {
                    if (--timeoutCount == 0) {
                        break;
                    }
                    VersionService.this.wait(1000L);
                }
            }
        } catch (InterruptedException e) {
            log(e);
        }
    }

    private void showToast(String msg) {
        final Message message = Message.obtain();
        message.what = SHOW_TOAST;
        message.getData().putString(KEY_MESSAGE, msg);
        mainHandler.sendMessage(message);
    }

    private void showProgress(String msg) {
        final Message message = Message.obtain();
        message.what = SHOW_PROGRESS;
        message.getData().putString(KEY_MESSAGE, msg);
        mainHandler.sendMessage(message);
    }

    private void hideProgress() {
        mainHandler.sendEmptyMessage(HIDE_PROGRESS);
    }

    private void showAskUpdateDialogAfterHadPatch(final File oldApk, final Version.PeekData peekData,
                                                  final Version.FetchData fetchData,
                                                  final File patch, final String patchMd5, final String patchSize,
                                                  final boolean startAfterInstall,
                                                  final int startDelayAfterInstalling) {
        final DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    new Thread("Apply-Patch-Thread") {
                        @Override
                        public void run() {
                            showProgress("请稍后, 正在下载并安装补丁...");
                            doUpdate(oldApk, patch, fetchData.from, fetchData.to,
                                    startAfterInstall, startDelayAfterInstalling);
                        }
                    }.start();
                } else {
                    SharedPreferences sp = getBackupStorage(VersionService.this);
                    saveData(sp, gson.toJson(peekData), gson.toJson(fetchData), patchMd5);

                    isEveryThingOk = true;
                }
                dialog.dismiss();
            }
        };

        showAskUpdateDialog(patchSize, fetchData.from, fetchData.to, l);
    }

    private void showAskUpdateDialog(final String patchSize, final Version.PathMd5 from,
                                     final Version.PathMd5 to, final DialogInterface.OnClickListener l) {
        if (isEveryThingOk) {
            isEveryThingOk = false;
            String patchDisplay;
            if (patchSize == null || patchSize.trim().isEmpty()) {
                patchDisplay = "";
            } else {
                try {
                    patchDisplay = "补丁大小: "
                            + FileUtils.byteCountToDisplaySize(Long.parseLong(patchSize));
                } catch (NumberFormatException e) {
                    log(e);
                    patchDisplay = "";
                }
            }
            String msg = "有版本可以更新啦, 是否又蠢蠢欲动了?\n"
                    + "当前版本: " + VersionManager.whatIs(from) + "\n"
                    + "目标版本: " + VersionManager.whatIs(to) + "\n"
                    + patchDisplay;
            final Message message = Message.obtain();
            message.what = SHOW_UPDATE_DIALOG;
            message.obj = l;
            message.getData().putString(KEY_MESSAGE, msg);
            mainHandler.sendMessage(message);
        }
    }

    private void doUpdate(File oldApk, File patch, Version.PathMd5 from, Version.PathMd5 to,
                          boolean startAfterInstall, int startDelay) {
        log("doUpdate...");
        final File oldSdcardApk = new File(Environment.getExternalStorageDirectory(),
                VersionManager.whatIs(from) + ".apk");
        final File newSdcardApk = new File(Environment.getExternalStorageDirectory(),
                VersionManager.whatIs(to) + ".apk");
        boolean applyPatch = true;
        try {
            FileUtils.copyFile(oldApk, oldSdcardApk, false);
        } catch (IOException e) {
            log(e);
            log("Copy old apk to sdcard failed!");
            applyPatch = false;
        }
        if (!Patch.apply(oldSdcardApk, newSdcardApk, patch)) {
            log("Apply patch failed!");
            applyPatch = false;
        }

        try {
            if (!to.md5.equals(VersionManager.md5(newSdcardApk))) {
                log("Target generate apk md5 check failed!");
                applyPatch = false;
            }
        } catch (Exception e) {
            log(e);
            log("Target generate apk md5 check failed!");
            applyPatch = false;
        }

        boolean result = true;
        if (!oldSdcardApk.delete()) {
            result = false;
        }
        if (!patch.delete()) {
            result = false;
        }
        if (!result) {
            log("Clean patch failed!");
        }

        if (applyPatch) {
            saveData(getBackupStorage(this), "", "", "");
            if (startAfterInstall) {
                Intent intent = new Intent(getApplicationContext(), StartReceiver.class);
                intent.setAction("action.start.after.install");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                if (alarmManager != null) {
                    showToast("应用将稍后自行启动");
                    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + startDelay, pendingIntent);
                }
            }
            hideProgress();
            log("All prepare time:" + System.currentTimeMillis());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(newSdcardApk), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            hideProgress();
            showToast("应用更新补丁失败");
        }
        isEveryThingOk = true;
        synchronized(VersionService.this) {
            VersionService.this.notifyAll();
        }
    }

    private static void log(Exception e) {
        e.printStackTrace();
    }

    private static void log(String x) {
        System.err.println(x);
    }

    private static final class MainHandler extends Handler {
        private ProgressDialog dlg;
        private Context context;

        public MainHandler(Context context) {
            super(Looper.getMainLooper());
            this.context = context;
        }

        public MainHandler setContext(Context context) {
            this.context = context;
            return this;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_UPDATE_DIALOG: {
                    AlertDialog alert = new AlertDialog.Builder(context, R.style.AppAlertDialogTheme)
                            .setPositiveButton("马上更新", (DialogInterface.OnClickListener) msg.obj)
                            .setNegativeButton("算了吧", (DialogInterface.OnClickListener) msg.obj)
                            .setMessage(msg.getData().getString(KEY_MESSAGE, ""))
                            .setTitle("温馨提示").setCancelable(false).create();
                    final Window window = alert.getWindow();
                    configWindow(window);
                    alert.show();
                }
                    break;
                case SHOW_PROGRESS: {
                    if (dlg == null || !dlg.isShowing()) {
                        dlg = new ProgressDialog(context);
                        dlg.setTitle("温馨提示");
                        String message = msg.getData().getString(KEY_MESSAGE, "");
                        if (message != null && !message.trim().isEmpty()) {
                            dlg.setMessage(message);
                        }
                        dlg.setIndeterminate(true);
                        dlg.setCancelable(false);
                        dlg.setOnCancelListener(null);
                        final Window window = dlg.getWindow();
                        configWindow(window);
                        dlg.show();
                    }
                }
                    break;
                case HIDE_PROGRESS: {
                    if (dlg != null && dlg.isShowing()) {
                        dlg.dismiss();
                        dlg = null;
                    }
                }
                    break;
                case SHOW_TOAST: {
                    String message = msg.getData().getString(KEY_MESSAGE, "");
                    if (message != null && !message.trim().isEmpty()) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown type");
            }
        }

        private void configWindow(Window window) {
            if (window != null) {
//                window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                window.setType(WindowManager.LayoutParams.TYPE_TOAST);
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            }
        }
    }

    private static final class GzipRequestInterceptor implements Interceptor {
        @Override
        public Response intercept(final Chain chain) throws IOException {
            final Request originalRequest = chain.request();
            if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
                return chain.proceed(originalRequest);
            }

            final Request compressedRequest = originalRequest.newBuilder()
                    .header("Content-Encoding", "gzip")
                    .method(originalRequest.method(), forceContentLength(gzip(originalRequest.body())))
                    .build();
            return chain.proceed(compressedRequest);
        }

        private RequestBody gzip(final RequestBody body) {
            return new RequestBody() {
                @Override
                public MediaType contentType() {
                    return body.contentType();
                }

                @Override
                public long contentLength() {
                    return -1; // We don't know the compressed length in advance!
                }

                @Override
                public void writeTo(final BufferedSink sink) throws IOException {
                    final BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                    body.writeTo(gzipSink);
                    gzipSink.close();
                }
            };
        }

        /** https://github.com/square/okhttp/issues/350 */
        private RequestBody forceContentLength(final RequestBody requestBody) throws IOException {
            final Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            return new RequestBody() {
                @Override
                public MediaType contentType() {
                    return requestBody.contentType();
                }

                @Override
                public long contentLength() {
                    return buffer.size();
                }

                @Override
                public void writeTo(BufferedSink sink) throws IOException {
                    sink.write(buffer.snapshot());
                }
            };
        }
    }
}
