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

import android.os.Bundle;
import android.os.RemoteException;

/**
 * @author John Kenrinus Lee
 * @version 2016-05-18
 */
public final class VerbalService implements IService {
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

    private VerbalService() {}

    public String description() throws RemoteException {
        return VerbalService.class.getSimpleName();
    }

    // 字符串字面值并未作为常量处理, 以最终发布文档描述为准
    @Override
    public int request(String url, Bundle bundle) throws RemoteException {
        if ("description".equals(url.substring(url.lastIndexOf("/") + 1))) {
            bundle.putString("result", description());
            return IService.RESULT_OK;
        }
        return IService.RESULT_ERROR_UNKNOWN;
    }
}
