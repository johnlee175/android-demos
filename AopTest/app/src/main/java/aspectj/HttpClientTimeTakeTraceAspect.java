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
package aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import android.util.Log;

/**
 * @author John Kenrinus Lee
 * @version 2016-06-20
 */
@Aspect
public class HttpClientTimeTakeTraceAspect {
    private static final String TAG = "TimeTakeTrace";

    @Pointcut("call( * cz.msebera.android.httpclient.client.HttpClient+.execute(..) )")
    public void pointcut_HttpClient_execute_Call() {}

    @Around("pointcut_HttpClient_execute_Call()")
    public Object around_HttpClient_execute_Call(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.w(TAG, "Where intercepted: " + joinPoint.getSignature());
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        Log.w(TAG, "Time take:  " + (System.currentTimeMillis() - start));
        return result;
    }
}
