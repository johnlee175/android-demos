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

import com.johnsoft.app.services.IChannelService;
import com.johnsoft.app.services.IService;

import android.os.RemoteException;
import android.util.Log;

/**
 * @author John Kenrinus Lee
 * @version 2016-05-18
 */
public final class ChannelService extends IChannelService.Stub implements IService {
    private static final String LOG_TAG = "IChannelService";
    public static final String SERVICE_NAME = "IChannelService";
    private static volatile ChannelService channelService;

    public static ChannelService getDefault() {
        if (channelService == null) {
            synchronized(ChannelService.class) {
                if (channelService == null) {
                    channelService = new ChannelService();
                }
            }
        }
        return channelService;
    }

    private ChannelService() {}

    @Override
    public String description() throws RemoteException {
        return ChannelService.class.getSimpleName();
    }

    @Override
    public void unload() {
        // ignore it
    }

    @Override
    public void onBinderProxyAdded() {
        // ignore it
    }

    @Override
    public void onBinderProxyRemoved() {
        // ignore it
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
        final ChannelService service = ChannelService.getDefault();
        service.open();
        ServicesManagerNative.getDefault().addService(SERVICE_NAME, service);
    }
}
