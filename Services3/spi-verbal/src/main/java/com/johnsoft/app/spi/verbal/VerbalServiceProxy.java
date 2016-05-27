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
package com.johnsoft.app.spi.verbal;

import java.lang.reflect.Method;

/**
 * @author John Kenrinus Lee
 * @version 2016-05-27
 */
public class VerbalServiceProxy {
    private Object objVerbalService;

    public VerbalServiceProxy(Object objVerbalService) {
        this.objVerbalService = objVerbalService;
    }

    public String call_ChannelService_description() {
        try {
            final Method method =
                    objVerbalService.getClass().getDeclaredMethod("delegate_ChannelService_description");
            return (String)method.invoke(objVerbalService);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
