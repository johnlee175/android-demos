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
package com.version.increment.patch;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.support.annotation.NonNull;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @author John Kenrinus Lee
 * @version 2016-11-24
 */
public class ContainerConverterFactory extends Converter.Factory {
    /**
     * @throws IllegalArgumentException if call with empty parameter
     */
    public static ContainerConverterFactory create(Converter.Factory...factories) {
        final ContainerConverterFactory factory = new ContainerConverterFactory();
        final int length = factories.length;
        if (length == 0) {
            throw new IllegalArgumentException("Empty container converter factory!");
        }
        for (int i = 0; i < length; ++i) {
            factory.addFactory(factories[i]);
        }
        return factory;
    }

    private final List<Converter.Factory> factories = new ArrayList<>();

    public void addFactory(@NonNull Converter.Factory factory) {
        if (factory != null) {
            factories.add(factory);
        }
    }

    public void removeFactory(@NonNull Converter.Factory factory) {
        if (factory != null) {
            factories.remove(factory);
        }
    }

    public boolean containsFactory(Converter.Factory factory) {
        return factories.contains(factory);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        final List<Converter.Factory> factoryList = factories;
        final int size = factoryList.size();
        Converter.Factory factory;
        Converter<ResponseBody, ?> converter;
        for (int i = 0; i < size; ++i) {
            factory = factoryList.get(i);
            converter = factory.responseBodyConverter(type, annotations, retrofit);
            if (converter != null) {
                return converter;
            }
        }
        return super.responseBodyConverter(type, annotations, retrofit);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations, Retrofit retrofit) {
        final List<Converter.Factory> factoryList = factories;
        final int size = factoryList.size();
        Converter.Factory factory;
        Converter<?, RequestBody> converter;
        for (int i = 0; i < size; ++i) {
            factory = factoryList.get(i);
            converter = factory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
            if (converter != null) {
                return converter;
            }
        }
        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }

    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        final List<Converter.Factory> factoryList = factories;
        final int size = factoryList.size();
        Converter.Factory factory;
        Converter<?, String> converter;
        for (int i = 0; i < size; ++i) {
            factory = factoryList.get(i);
            converter = factory.stringConverter(type, annotations, retrofit);
            if (converter != null) {
                return converter;
            }
        }
        return super.stringConverter(type, annotations, retrofit);
    }
}
