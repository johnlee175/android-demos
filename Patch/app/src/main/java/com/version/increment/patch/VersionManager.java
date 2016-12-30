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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.gson.Gson;

import android.content.Context;
import android.os.Build;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author John Kenrinus Lee
 * @version 2016-11-24
 */
public class VersionManager {
    private static Version version;

    public static void initWith(Context context, OkHttpClient client, Gson gson, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(ContainerConverterFactory.create(GsonConverterFactory.create(gson)))
            .build();
        version = retrofit.create(Version.class);
    }

    public static Version.ResponseResult peekVersionInfo(Version.PeekData peekData) {
        if (version == null) {
            throw new IllegalStateException("Please call initWith() to initialize!");
        }
        final Call<Version.ResponseResult> call = version.peekVersionInfo(peekData);
        try {
            final Response<Version.ResponseResult> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                System.err.println("peekVersionInfo: " + response.errorBody().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] fetchPatch(Version.FetchData fetchData, File targetPath) {
        if (version == null) {
            throw new IllegalStateException("Please call initWith() to initialize!");
        }
        try {
            final Call<ResponseBody> call = version.fetchPatch(fetchData);
            final Response<ResponseBody> response = call.execute();
            final Headers headers = response.headers();
            if (response.isSuccessful()) {
                String code = headers.get("code");
                String message = headers.get("message");
                if ("0".equals(code.trim())) {
                    InputStream is = new BufferedInputStream(response.body().byteStream());
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(targetPath));
                    FileUtils.copy(is, os);
                    FileUtils.closeQuietly(os);
                    FileUtils.closeQuietly(is);
                    if (targetPath.exists() && targetPath.length() > 0) {
                        String should = headers.get("patch-md5");
                        String actual = md5(targetPath);
                        if (actual.equals(should)) {
                            return new String[] { actual, headers.get("target-md5") };
                        }
                    }
                } else {
                    System.err.println("fetchPatch: { code: " + code + ", message: " + message + " }");
                }
            } else {
                System.err.println("fetchPatch: " + response.errorBody().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f' };

    public static String md5(File file) throws NoSuchAlgorithmException, IOException {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            final byte[] originBytes = FileUtils.toByteArray(is);
            final byte[] md5Bytes = MessageDigest.getInstance("MD5").digest(originBytes);
            final char[] resultCharArray = new char[md5Bytes.length << 1];
            int index = 0;
            for (byte b : md5Bytes) {
                resultCharArray[index++] = HEX_DIGITS[b >>> 4 & 0xf];
                resultCharArray[index++] = HEX_DIGITS[b & 0xf];
            }
            return new String(resultCharArray);
        } finally {
            FileUtils.closeQuietly(is);
        }
    }

    public static String whatIs(Version.PathMd5 pathMd5) {
        if (pathMd5 == null || pathMd5.path == null || pathMd5.path.trim().isEmpty()) {
            throw new IllegalArgumentException("whatIs: Invalid pathMd5!");
        }
        return pathMd5.path.split("/")[0];
    }

    public static String getAndroidDeviceInfo() {
        //        return Build.FINGERPRINT;
        return Build.BRAND + " " + Build.MANUFACTURER + " " + Build.MODEL + " " + Build.ID;
    }

    public static String getAndroidVersionInfo() {
        return "android-" + Build.VERSION.RELEASE + " api-" + String.valueOf(Build.VERSION.SDK_INT);
    }
}
