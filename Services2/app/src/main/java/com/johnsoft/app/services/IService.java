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

import android.os.Bundle;
import android.os.RemoteException;

/**
 * @author John Kenrinus Lee
 * @version 2016-05-19
 */
public interface IService {
    int RESULT_OK = 0;
    int RESULT_ERROR_UNKNOWN = -1;
    int RESULT_ERROR_ILLEGAL_ARGUMENT = -2;

    int request(String url, Bundle bundle) throws RemoteException;
}
