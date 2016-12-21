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
package com.johnsoft.app.mymvp;

import java.util.HashMap;

import org.rxbus.AndroidSchedulers;
import org.rxbus.RxBus;
import org.rxbus.Subscribe;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

/**
 * 主程序。
 *
 * @author John Kenrinus Lee
 * @version 2016-05-30
 */
public class App extends Application {
    private static final String TAG = "App";
    private static final String KEY_MAIN_HANDLER = "main_handler";
    private static App app;
    private final HashMap<String, Object> applicationMap = new HashMap<>();

    public App() {
    }

    public static App self() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        applicationMap.put(KEY_MAIN_HANDLER, new Handler(Looper.getMainLooper()));
        RxBus.singleInstance.addSchedulerWithId(Subscribe.SCHEDULER_FOR_FIRST_CUSTOM, AndroidSchedulers.mainThread());
    }

    public Handler getMainHandler() {
        return (Handler) applicationMap.get(KEY_MAIN_HANDLER);
    }
}
