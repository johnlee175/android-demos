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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

    public static String getVersionName(Context context) {
//        return BuildConfig.VERSION_NAME;
        try {
            PackageInfo pinfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(),
                            PackageManager.GET_CONFIGURATIONS);
            return pinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static int getVersionCode(Context context) {
//        return BuildConfig.VERSION_CODE;
        try {
            PackageInfo pinfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(),
                            PackageManager.GET_CONFIGURATIONS);
            return pinfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private static volatile VersionParts sVersionParts;

    public static VersionParts getVersionParts(Context context) {
        if (sVersionParts == null) {
            synchronized (VersionParts.class) {
                if (sVersionParts == null) {
                    // format like: 1.0.fc56486_develop@20161208-090311-441&a45e60bf4695$3121184
                    String versionName = getVersionName(context);
                    if (versionName == null || versionName.trim().isEmpty()) {
                        versionName = BuildConfig.VERSION_NAME;
                        if (versionName.trim().isEmpty()) {
                            sVersionParts = new VersionParts();
                            return sVersionParts;
                        }
                    }
                    final String product = "robokit";
                    String[] temps = versionName.split("\\.");
                    final String majorNumber = temps[0];
                    final String minorNumber = temps[1];
                    int idx1 = temps[2].indexOf('_');
                    final String lastCommitId = temps[2].substring(0, idx1);
                    int idx2 = temps[2].indexOf('@');
                    final String branch = temps[2].substring(idx1 + 1, idx2);
                    idx1 = temps[2].indexOf('&');
                    final String strDateTime = temps[2].substring(idx2 + 1, idx1);
                    idx2 = temps[2].indexOf('$');
                    final String buildMachineAddress = temps[2].substring(idx1 + 1, idx2);
                    final String mappingCode = temps[2].split("\\$")[1];
                    sVersionParts = new VersionParts(majorNumber, minorNumber, product, branch, lastCommitId,
                            strDateTime, buildMachineAddress, mappingCode,
                            BuildConfig.BUILD_TYPE, BuildConfig.FLAVOR,
                            getAndroidVersionInfo(), getAndroidDeviceInfo());
                }
            }
        }
        return sVersionParts;
    }

    public static final class VersionParts {
        public final String majorNumber;
        public final String minorNumber;
        public final String product;
        public final String branch;
        public final String lastCommitId;
        public final String strDateTime;
        public final String buildMachineAddress;
        public final String mappingCode;
        public final String buildType;
        public final String flavor;
        public final String androidVersion;
        public final String androidDevice;

        private VersionParts() {
            majorNumber = "";
            minorNumber = "";
            product = "";
            branch = "";
            lastCommitId = "";
            strDateTime = "";
            buildMachineAddress = "";
            mappingCode = "";
            buildType = "";
            flavor = "";
            androidVersion = "";
            androidDevice = "";
        }

        private VersionParts(String majorNumber, String minorNumber, String product, String branch,
                             String lastCommitId, String strDateTime, String buildMachineAddress,
                             String mappingCode, String buildType, String flavor,
                             String androidVersion, String androidDevice) {
            if (majorNumber == null) {
                majorNumber = "";
            }
            this.majorNumber = majorNumber;
            if (minorNumber == null) {
                minorNumber = "";
            }
            this.minorNumber = minorNumber;
            if (product == null) {
                product = "";
            }
            this.product = product;
            if (branch == null) {
                branch = "";
            }
            this.branch = branch;
            if (lastCommitId == null) {
                lastCommitId = "";
            }
            this.lastCommitId = lastCommitId;
            if (strDateTime == null) {
                strDateTime = "";
            }
            this.strDateTime = strDateTime;
            if (buildMachineAddress == null) {
                buildMachineAddress = "";
            }
            this.buildMachineAddress = buildMachineAddress;
            if (mappingCode == null) {
                mappingCode = "";
            }
            this.mappingCode = mappingCode;
            if (buildType == null) {
                buildType = "";
            }
            this.buildType = buildType;
            if (flavor == null) {
                flavor = "";
            }
            this.flavor = flavor;
            if (androidVersion == null) {
                androidVersion = "";
            }
            this.androidVersion = androidVersion;
            if (androidDevice == null) {
                androidDevice = "";
            }
            this.androidDevice = androidDevice;
        }

        @Override
        public String toString() {
            return "VersionParts{"
                    + "majorNumber='" + majorNumber + '\''
                    + ", minorNumber='" + minorNumber + '\''
                    + ", product='" + product + '\''
                    + ", branch='" + branch + '\''
                    + ", lastCommitId='" + lastCommitId + '\''
                    + ", strDateTime='" + strDateTime + '\''
                    + ", buildMachineAddress='" + buildMachineAddress + '\''
                    + ", mappingCode='" + mappingCode + '\''
                    + ", buildType='" + buildType + '\''
                    + ", flavor='" + flavor + '\''
                    + ", androidVersion='" + androidVersion + '\''
                    + ", androidDevice='" + androidDevice + '\''
                    + '}';
        }
    }
}
