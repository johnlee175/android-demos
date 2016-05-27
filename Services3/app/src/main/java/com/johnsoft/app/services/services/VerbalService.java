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
package com.johnsoft.app.services.services;

import java.io.File;

import com.johnsoft.app.services.App;
import com.johnsoft.app.services.AssetsToAppFilesDir;
import com.johnsoft.app.services.IVerbalService;
import com.johnsoft.app.services.PluginManager;
import com.johnsoft.app.services.Settings;

import android.os.RemoteException;
import dalvik.system.DexClassLoader;

/**
 * @author John Kenrinus Lee
 * @version 2016-05-27
 */
public final class VerbalService extends IVerbalService.Stub {
    public static final String SERVICE_NAME = "IVerbalService";
    private static volatile VerbalService verbalService;

    public static VerbalService getDefault() {
        if (!Settings.verbalServiceEnabled) {
            return null;
        }
        if (verbalService == null) {
            synchronized(VerbalService.class) {
                if (verbalService == null) {
                    verbalService = new VerbalService();
                }
            }
        }
        return verbalService;
    }

    private VerbalService() {
        File apkFile = AssetsToAppFilesDir.copy(App.getApplication(), "verbal.apk", false);
        // May be the apk is not in assets, is from net site? Just download it and register.
        PluginManager.registerModule(SERVICE_NAME, apkFile);
    }

    @Override
    public String description() throws RemoteException {
        if (Settings.PLUGIN_FIRST) {
            final DexClassLoader classLoader = PluginManager.getClassLoader(SERVICE_NAME);
            if (classLoader != null) {
                String result = (String) PluginManager.invokeMethod(classLoader,
                        "com.johnsoft.app.spi.verbal.Verbal", "description", true, Object.class, this);
                if (result != null) {
                    return result;
                }
            }
        }
        return VerbalService.class.getSimpleName(); /* default */
    }

    public String delegate_ChannelService_description() { // dependent service
        try {
            return ChannelService.getDefault().description();
        } catch (Exception e) {
            return "";
        }
    }

    public static void main() {
        final VerbalService service = VerbalService.getDefault();
        ServicesManagerNative.getDefault().addService(SERVICE_NAME, service);
    }
}
