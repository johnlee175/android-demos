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

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import dalvik.system.DexClassLoader;

/**
 * @author John Kenrinus Lee
 * @version 2016-05-25
 */
public final class PluginManager {
    private PluginManager() {}

    private static final ConcurrentHashMap<String, DexClassLoader> moduleApkLoaderMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, File> moduleApkFileMap = new ConcurrentHashMap<>();

    public static DexClassLoader getClassLoader(String moduleName) {
        DexClassLoader classLoader = moduleApkLoaderMap.get(moduleName);
        if (classLoader == null) {
            File apkFile = moduleApkFileMap.get(moduleName);
            if (apkFile != null && apkFile.exists() && apkFile.isFile() && apkFile.canRead()) {
                // apk is OK: No matter whether it's multi-dex/dalvik/art (dex/oat) (jit/aot) or not
                // dex is OK: classes.jar/xxx.class == "dx --dex --output" ==> classes.dex
                // Notice: aar and jar not work
                final String pathForDexOrApk = apkFile.getAbsolutePath();
                final Context appContext = App.getApplication();
                final ApplicationInfo applicationInfo = appContext.getApplicationInfo();
                final String optimizedDexOutputDirectory = applicationInfo.dataDir;
                final String nativeLibraryPath = applicationInfo.nativeLibraryDir;
                classLoader = new DexClassLoader(pathForDexOrApk,
                        optimizedDexOutputDirectory, nativeLibraryPath, PluginManager.class.getClassLoader());
                moduleApkLoaderMap.put(moduleName, classLoader);
            }
        }
        return classLoader;
    }

    public static void invalidateClassLoaderCache(String moduleName) {
        moduleApkLoaderMap.remove(moduleName);
    }

    public static void registerModule(String moduleName, File apkFile) {
        // the apk may be downloaded from net site, or copy from assets
        moduleApkFileMap.put(moduleName, verifyAndTransform(apkFile));
    }

    public static void unregisterModule(String moduleName) {
        moduleApkFileMap.remove(moduleName);
    }

    public static Object invokeMethod(DexClassLoader classLoader, String classFullName, String methodName,
                                      boolean fromDeclaredMethods, Object...parameters) {
        try {
            // every service will bridge from this method, you can hook
            // it for monitor
            if (parameters.length % 2 != 0) {
                throw new IllegalArgumentException("Method parameters is not matched with class, object pair.");
            }
            final Class<?> clazz = classLoader.loadClass(classFullName);
            final Class<?>[] paramClasses = new Class<?>[parameters.length / 2];
            final Object[] paramObjects = new Object[parameters.length / 2];
            for (int i = 0, ci = 0, oi = 0; i < parameters.length; ++i) {
                if (i % 2 == 0) {
                    paramClasses[ci++] = (Class<?>)parameters[i];
                } else {
                    paramObjects[oi++] = parameters[i];
                }
            }
            final Method method;
            if (fromDeclaredMethods) {
                method = clazz.getDeclaredMethod(methodName, paramClasses);
            } else {
                method = clazz.getMethod(methodName, paramClasses);
            }
            method.setAccessible(true);
            return method.invoke(clazz.newInstance(), paramObjects);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Security needs
    private static File verifyAndTransform(File apkFile) {
        // md5sum check
        // signature check or password check when unzip or transform
        // transform file content format if needed
        return apkFile;
    }
}
