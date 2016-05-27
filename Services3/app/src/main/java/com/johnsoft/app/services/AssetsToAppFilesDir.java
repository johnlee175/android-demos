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
package com.johnsoft.app.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

/**
 * @author John Kenrinus Lee
 * @version 2016-05-25
 */
public final class AssetsToAppFilesDir {
    private AssetsToAppFilesDir() {}

    public static File copy(Context context, String path, boolean recover) {
        if (context == null || path == null) return null;
        String[] segments = path.split(File.separator);
        if (segments.length == 0) return null;
        String fileName = segments[segments.length - 1];
        Context appContext = context.getApplicationContext();
        File toFile;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            toFile = new File(appContext.getExternalFilesDir(null), fileName);
        } else {
            toFile = new File(appContext.getFilesDir(), fileName);
        }
        AssetManager assetManager = appContext.getAssets();
        if (!toFile.exists() || recover) {
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                bis = new BufferedInputStream(assetManager.open(path));
                bos = new BufferedOutputStream(new FileOutputStream(toFile));
                byte[] buffer = new byte[4096];
                int len;
                while ((len = bis.read(buffer, 0, buffer.length)) > 0) {
                    bos.write(buffer, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException ignored) {
                    }
                }
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        return toFile;
    }
}
