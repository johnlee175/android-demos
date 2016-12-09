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

/**
 * @author John Kenrinus Lee
 * @version 2016-11-23
 */
public class Patch {
    static {
        System.loadLibrary("patch");
    }

    public static boolean apply(File oldApk, File newApk, File patch) {
        if (isValidExistFile(oldApk) && isValidExistFile(patch) && isValidNewFile(newApk)) {
            return apply(oldApk.getAbsolutePath(), newApk.getAbsolutePath(), patch.getAbsolutePath()) == 0;
        }
        return false;
    }

    private static boolean isValidExistFile(File file) {
        return file != null && file.exists() && file.isFile() && file.canRead() && file.length() > 0;
    }

    private static boolean isValidNewFile(File file) {
        if (file == null) {
            return false;
        }
        if (!file.exists()) {
            return true;
        }
        if (file.isDirectory()) {
            return false;
        }
        return (file.delete() || file.canWrite());
    }

    private static native int apply(String oldApk, String newApk, String patch);
}
