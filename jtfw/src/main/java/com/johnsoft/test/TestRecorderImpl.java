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
package com.johnsoft.test;

import java.io.File;
import java.io.IOException;

import org.ter.antlr4.TerWriter;
import org.ter.antlr4.TestEvent;

/**
 * Thread safe
 *
 * @author John Kenrinus Lee
 * @version 2016-12-29
 */
public class TestRecorderImpl implements TestRecorder,
        TestRecorder.TestTrigger, TestRecorder.TestVerifier {
    private TerWriter writer;
    private boolean recording;
    private String currentCase;

    @Override
    public synchronized boolean isRecording() {
        return recording;
    }

    @Override
    public synchronized void startRecord(File outputPath, String packageName) {
        recording = true;
        writer = new TerWriter(outputPath, packageName);
    }

    @Override
    public synchronized void stopRecord() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            recording = false;
        }
    }

    @Override
    public synchronized String currentCase() {
        return currentCase;
    }

    @Override
    public synchronized void newCase(String mark) {
        currentCase = mark;
        writer.recordCase(mark);
    }

    @Override
    public synchronized void finishCase() {
        currentCase = null;
    }

    @Override
    public synchronized void recordTriggerEvent(TestEvent event) {
        writer.recordTrigger(event);
    }

    @Override
    public synchronized void recordVerifierEvent(TestEvent event) {
        writer.recordVerifier(event);
    }
}
