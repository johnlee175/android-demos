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

import java.util.concurrent.ConcurrentHashMap;

import com.johnsoft.app.services.IServicesManager;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author John Kenrinus Lee
 * @version 2016-05-18
 */
public final class ServicesManagerNative extends IServicesManager.Stub {
    private static final String LOG_TAG = "IServicesManager";
    private static volatile ServicesManagerNative servicesManager;

    public static ServicesManagerNative getDefault() {
        if (servicesManager == null) {
            synchronized(ServicesManagerNative.class) {
                if (servicesManager == null) {
                    servicesManager = new ServicesManagerNative();
                }
            }
        }
        return servicesManager;
    }

    private final ConcurrentHashMap<String, IBinder> binderMap = new ConcurrentHashMap<>();

    private ServicesManagerNative() {}

    public void addService(String serviceName, IBinder iBinder) {
        if (binderMap.put(serviceName, iBinder) == null) {
            if (Log.isLoggable(LOG_TAG, Log.INFO)) {
                Log.i(LOG_TAG, "ServicesManagerNative#addService(" + serviceName + ", " + iBinder + ")");
            }
        } else {
            if (Log.isLoggable(LOG_TAG, Log.INFO)) {
                Log.i(LOG_TAG, "ServicesManagerNative#modifyService(" + serviceName + ", " + iBinder + ")");
            }
        }
    }

    public void removeService(String serviceName) {
        if (binderMap.remove(serviceName) != null) {
            if (Log.isLoggable(LOG_TAG, Log.INFO)) {
                Log.i(LOG_TAG, "ServicesManagerNative#removeService(" + serviceName + ")");
            }
        }
    }

    @Override
    public IBinder getService(String serviceName) throws RemoteException {
        if (Log.isLoggable(LOG_TAG, Log.INFO)) {
            Log.i(LOG_TAG, "ServicesManagerNative#getService(" + serviceName + ")");
        }
        return binderMap.get(serviceName);
    }
}
