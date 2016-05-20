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

import com.johnsoft.app.services.App;
import com.johnsoft.app.services.IService;
import com.johnsoft.app.services.IVerbalService;
import com.johnsoft.app.services.SharedReference;

import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author John Kenrinus Lee
 * @version 2016-05-18
 */
public final class VerbalService extends IVerbalService.Stub implements IService {
    private static final String LOG_TAG = "IVerbalService";
    public static final String SERVICE_NAME = "IVerbalService";
    private static volatile VerbalService verbalService;

    public static VerbalService getDefault() {
        if (verbalService == null) {
            synchronized(VerbalService.class) {
                if (verbalService == null) {
                    verbalService = new VerbalService();
                }
            }
        }
        return verbalService;
    }

    private final SharedReference reference = new SharedReference(new Runnable() {
        @Override
        public void run() {
            final Handler handler = App.getApplication().getWorkHandler();
            handler.removeCallbacks(disableRunnable);
            handler.post(enableRunnable);

        }
    }, new Runnable() {
        @Override
        public void run() {
            Log.i(LOG_TAG, SERVICE_NAME + " is schedule to close later @_@");
            final Handler handler = App.getApplication().getWorkHandler();
            handler.postDelayed(disableRunnable, 2 * 60 * 1000L);
        }
    });

    private final Runnable enableRunnable = new Runnable() {
        @Override
        public void run() {
            open();
            // may be add duplicate, but it's ok because of the same object;
            ServicesManagerNative.getDefault().addService(SERVICE_NAME, VerbalService.getDefault());
        }
    };
    private final Runnable disableRunnable = new Runnable() {
        @Override
        public void run() {
            ServicesManagerNative.getDefault().removeService(SERVICE_NAME);
            close();
        }
    };

    private VerbalService() {}

    @Override
    public String description() throws RemoteException {
        return VerbalService.class.getSimpleName();
    }

    @Override
    public void unload() { //dangerous
        reference.reduceReferenceOrNoOp();
        Log.i(LOG_TAG, SERVICE_NAME + " reduce reference from unload @_@ " + reference.getRefCount());
    }

    @Override
    public void onBinderProxyAdded() {
        reference.addReference();
        Log.i(LOG_TAG, SERVICE_NAME + " add reference from onBinderProxyAdded @_@ " + reference.getRefCount());
    }

    @Override
    public void onBinderProxyRemoved() {
        reference.reduceReference();
        Log.i(LOG_TAG, SERVICE_NAME + " reduce reference from onBinderProxyRemoved @_@ " + reference.getRefCount());
    }

    @Override
    public void open() {
        Log.i(LOG_TAG, SERVICE_NAME + " is opening @_@");
    }

    @Override
    public void close() {
        Log.i(LOG_TAG, SERVICE_NAME + " is closing @_@");
    }

    public static void main() {
        // no open
        ServicesManagerNative.getDefault().addService(SERVICE_NAME, VerbalService.getDefault());
    }
}
