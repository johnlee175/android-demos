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

import com.johnsoft.app.services.IService;
import com.johnsoft.app.services.ServicesInterface;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author John Kenrinus Lee
 * @version 2016-05-18
 */
public final class ServicesManagerNative extends ServicesInterface.Stub {
    private static final String LOG_TAG = "ServicesInterface";

    public static final String URL_CHANNEL = "service/channel";
    public static final String URL_VERBAL = "service/verbal";

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

    private ServicesManagerNative() {}

    @Override
    public int request(String url, Bundle bundle) throws RemoteException {
        if (url == null) {
            log("Error: url == null");
            return IService.RESULT_ERROR_ILLEGAL_ARGUMENT;
        }
        if (url.startsWith(URL_CHANNEL)) {
            log("Forwarding to ChannelService");
            return ChannelService.getDefault().request(url, bundle);
        } else if (url.startsWith(URL_VERBAL)) {
            log("Error: url == null");
            return VerbalService.getDefault().request(url, bundle);
        } else {
            return IService.RESULT_ERROR_UNKNOWN;
        }
    }

    private static void log(String message) {
        if (Log.isLoggable(LOG_TAG, Log.INFO)) {
            Log.i(LOG_TAG, message);
        }
    }
}
