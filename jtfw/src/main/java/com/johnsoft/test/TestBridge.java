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

import org.ter.antlr4.TestEvent;

/**
 * the facade of test framework
 *
 * @author John Kenrinus Lee
 * @version 2016-12-01
 */
public final class TestBridge {
    private static volatile TestPlayerImpl player;
    private static volatile TestRecorderImpl recorder;

    private static TestPlayerImpl getSinglePlayer() {
        if (player == null) {
            synchronized (TestPlayerImpl.class) {
                if (player == null) {
                    player = new TestPlayerImpl();
                }
            }
        }
        return player;
    }

    private static TestRecorderImpl getSingleRecorder() {
        if (recorder == null) {
            synchronized (TestRecorderImpl.class) {
                if (recorder == null) {
                    recorder = new TestRecorderImpl();
                }
            }
        }
        return recorder;
    }

    public static TestPlayer player() {
        return getSinglePlayer();
    }

    public static TestRecorder recorder() {
        return getSingleRecorder();
    }

    public static TestRecorder.TestTrigger trigger() {
        return getSingleRecorder();
    }

    public static TestRecorder.TestVerifier verifier() {
        return getSingleRecorder();
    }

    public static void recordTriggerEvent(TestEvent event) {
        if (recorder().isRecording()) {
            trigger().recordTriggerEvent(event);
        }
    }

    public static void recordVerifierEvent(TestEvent event) {
        if (event.fingerprint == null || event.fingerprint.trim().isEmpty()) {
            throw new IllegalArgumentException("TestEvent.fingerprint must be valid in recordVerifierEvent");
        }
        if (recorder().isRecording()) {
            verifier().recordVerifierEvent(event);
        } else {
            if (player().isPlaying()) {
                player().verify(event);
            }
        }
    }
}
