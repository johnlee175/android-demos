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

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * @author John Kenrinus Lee
 * @version 2016-05-20
 */
public class App extends Application {
    private static App app;

    public static App getApplication() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        createHandlers();
        startService(new Intent(this, ServicesManagerService.class));
    }

    private Handler mainHandler;
    private Handler workHandler;

    private void createHandlers() {
        mainHandler = new Handler(Looper.getMainLooper());
        final HandlerThread handlerThread = new HandlerThread("work");
        handlerThread.start();
        workHandler = new Handler(handlerThread.getLooper());
    }

    public Handler getMainHandler() {
        return mainHandler;
    }

    public Handler getWorkHandler() {
        return workHandler;
    }
}
