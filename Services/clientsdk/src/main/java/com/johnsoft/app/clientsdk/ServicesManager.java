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

import com.johnsoft.app.clientsdk.proxys.ChannelManager;
import com.johnsoft.app.clientsdk.proxys.VerbalManager;
import com.johnsoft.app.services.IServicesCallback;
import com.johnsoft.app.services.IServicesManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.util.ArrayMap;

/**
 * @author John Kenrinus Lee
 * @version 2016-05-18
 */
public final class ServicesManager {
    private ServicesManager() {}

    public static final String CHANNEL_SERVICE = "IChannelService";
    public static final String VERBAL_SERVICE = "IVerbalService";

    private static final byte[] WAIT_LOCK = new byte[0];
    private static final byte[] MAP_LOCK = new byte[0];
    private static final ArrayMap<String, Object> servicesMap = new ArrayMap<>();
    private static final ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                servicesManager = IServicesManager.Stub.asInterface(service);
                if (servicesManager != null) {
                    servicesManager.asBinder().linkToDeath(new IBinder.DeathRecipient() {
                        @Override
                        public void binderDied() {
                            initServicesManager(appContext);
                        }
                    }, 0);
                    servicesManager.setServicesCallback(callback);
                    registerService(CHANNEL_SERVICE,
                            new ChannelManager(servicesManager.getService(CHANNEL_SERVICE)));
                    registerService(VERBAL_SERVICE,
                            new VerbalManager(servicesManager.getService(VERBAL_SERVICE)));
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } finally {
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
    private static final IServicesCallback callback = new IServicesCallback.Stub() {
        @Override
        public void onServiceUnload(String serviceName) throws RemoteException {
            if (lifeCycleChangedListener != null) {
                lifeCycleChangedListener.onServiceUnload(serviceName);
            }
        }
    };
    private static volatile IServicesManager servicesManager;
    private static volatile boolean servicesReady;
    private static volatile OnServicesLifeCycleChangedListener lifeCycleChangedListener;
    private static Context appContext;

    private static void registerService(String serviceName, Object iBinder) {
        synchronized(MAP_LOCK) {
            servicesMap.put(serviceName, iBinder);
        }
    }

    public static Object getService(String serviceName) {
        synchronized(MAP_LOCK) {
            return servicesMap.get(serviceName);
        }
    }

    public static VerbalManager getVerbalManager() {
        return (VerbalManager)getService(VERBAL_SERVICE);
    }

    public static ChannelManager getChannelManager() {
        return (ChannelManager)getService(CHANNEL_SERVICE);
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

    public static void initServicesManager(Context context) {
        appContext = context.getApplicationContext();
        Intent intent = new Intent();
        intent.setAction("com.baidu.intent.action.ServiceManagerService");
        intent.setPackage("com.johnsoft.app.services");
        if (!appContext.bindService(intent, conn, Context.BIND_AUTO_CREATE)) {
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
}
