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
package com.johnsoft.app.clientsdk;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * @author John Kenrinus Lee
 * @version 2016-05-18
 */
public final class ServicesManager {
    private ServicesManager() {}

    public static final String URL_CHANNEL = "service/channel";
    public static final String URL_VERBAL = "service/verbal";

    public static final int RESULT_OK = 0;
    public static final int RESULT_ERROR_UNKNOWN = -1;
    public static final int RESULT_ERROR_ILLEGAL_ARGUMENT = -2;

    private static final byte[] WAIT_LOCK = new byte[0];
    private static final ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                servicesManager = ServicesInterface.Stub.asInterface(service);
            }finally {
                servicesReady = true;
                synchronized(WAIT_LOCK) {
                    WAIT_LOCK.notifyAll();
                }
                if (lifeCycleChangedListener != null) {
                    lifeCycleChangedListener.onServicesReady();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            servicesManager = null;
        }
    };
    private static volatile ServicesInterface servicesManager;
    private static volatile boolean servicesReady;
    private static volatile OnServicesLifeCycleChangedListener lifeCycleChangedListener;
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static int request(String url, Bundle bundle) {
        if (servicesManager != null) {
            try {
                return servicesManager.request(url, bundle);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        System.err.println("servicesManager == null");
        return RESULT_ERROR_UNKNOWN;
    }

    public static Future<?> asyncRequest(final String url, final Bundle bundle, final OnServiceResponse resp) {
        return executorService.submit(new Runnable() {
            @Override
            public void run() {
                final int resultCode = request(url, bundle);
                if (resp != null) {
                    resp.onResponse(resultCode, bundle);
                }
            }
        });
    }

    public static void waitForServicesReady() {
        while(!servicesReady) {
            try {
                synchronized(WAIT_LOCK) {
                    WAIT_LOCK.wait(100L);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void initServicesManager(Context appContext) {
        Intent intent = new Intent();
        intent.setAction("com.baidu.intent.action.ServiceManagerService");
        intent.setPackage("com.johnsoft.app.services");
        if (!appContext.getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE)) {
            System.err.println("Bind Service ServiceManagerService failed!");
        }
    }

    public static void setOnServicesLifeCycleChangedListener(OnServicesLifeCycleChangedListener l) {
        lifeCycleChangedListener = l;
    }

    public interface OnServicesLifeCycleChangedListener {
        void onServicesReady();
        void onServiceUnload(String serviceName);
    }

    public interface OnServiceResponse {
        void onResponse(int resultCode, Bundle bundle);
    }
}
