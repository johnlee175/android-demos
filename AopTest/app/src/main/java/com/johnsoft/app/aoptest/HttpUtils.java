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
package com.johnsoft.app.aoptest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

/**
 * @author John Kenrinus Lee
 * @version 2016-06-19
 */
public final class HttpUtils {
    private HttpUtils() {}

    private static final Handler workHandler = createHandler("http-worker");

    private static Handler createHandler(String name) {
        HandlerThread thread = new HandlerThread(name);
        thread.start();
        return new Handler(thread.getLooper());
    }

    public static void printHttpRequestResultAsync(final String url) {
        workHandler.post(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                ByteArrayOutputStream outputStream = null;
                HttpURLConnection conn = null;
                boolean isConnected = false;
                try {
                    conn = (HttpURLConnection)new URL(url).openConnection();
                    conn.setRequestProperty("Accept", "text/html");
                    conn.setRequestMethod("GET");
                    conn.setInstanceFollowRedirects(true);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(30000);
                    conn.setReadTimeout(30000);
                    conn.connect();
                    isConnected = true;
                    inputStream = conn.getInputStream();
                    outputStream = new ByteArrayOutputStream();
                    byte[] bytes = new byte[4096];
                    int len;
                    while ((len = inputStream.read(bytes, 0, bytes.length)) > 0) {
                        outputStream.write(bytes, 0, len);
                    }
                    String result = new String(outputStream.toByteArray(), "UTF-8");
                    System.err.println(result);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException ignored) {
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException ignored) {
                        }
                    }
                    if (conn != null && isConnected) {
                        conn.disconnect();
                    }
                }
            }
        });
    }

    public static void printHttpClientASync(final Context context, final String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(context.getApplicationContext(), url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, String s,
                                  Throwable throwable) {
                System.err.println("code: " + i + "msg: " + s);
                if (throwable != null) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, String s) {
                System.err.println("code: " + i + "msg: \n" + s);
            }
        });
    }
}
