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
package com.johnsoft.app.clientsdk.proxys;

import com.johnsoft.app.services.IChannelService;

import android.os.IBinder;
import android.os.RemoteException;

/**
 * @author John Kenrinus Lee
 * @version 2016-05-18
 */
public final class ChannelManager {
    private final IChannelService channelService;

    public ChannelManager(IBinder iBinder) {
        if (iBinder != null) {
            channelService = IChannelService.Stub.asInterface(iBinder);
        } else {
            channelService = null;
        }
    }

    public String description() {
        try {
            if (channelService != null) {
                return channelService.description();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void unload() {
        try {
            if (channelService != null) {
                channelService.unload();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
