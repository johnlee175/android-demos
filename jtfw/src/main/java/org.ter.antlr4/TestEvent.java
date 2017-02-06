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
package org.ter.antlr4;

import java.util.LinkedList;
import java.util.List;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-01
 */
public class TestEvent implements TerTestCaseItem {
    private static final int QUEUE_SIZE = 127;
    private static final byte[] sLock = new byte[0];
    private static final LinkedList<TestEvent> sQueue = new LinkedList<>();

    public static TestEvent obtain() {
        synchronized (sLock) {
            if (sQueue.size() == 0) {
                return new TestEvent().generate();
            } else {
                return sQueue.removeFirst().generate();
            }
        }
    }

    public static boolean recycle(TestEvent event) {
        synchronized (sLock) {
            if (event != null && sQueue.size() <= QUEUE_SIZE) {
                for (TestEvent entry : sQueue) {
                    if (entry != null && entry == event) {
                        return false;
                    }
                }
                sQueue.addLast(event.clear());
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean isValid(TestEvent event) {
        final String id = event.id;
        final String type = event.type;
        return id != null && !id.trim().isEmpty()
                && type != null && !type.trim().isEmpty()
                && event.when > 0L && event.data != null;
    }

    /** the unique identifier of event */
    public String id;
    /** the event happened time */
    public long when;
    /** event type, indicate which {@link TestUnit} will handle it */
    public String type;
    /** if a event type mapping multi-case, switch it with the code */
    public int code;
    /** some event just can be consumed once, this field for that situation */
    public boolean consumed;
    /** human made or program made or others */
    public int triggerCause;
    /** the event source, maybe indicate where the event came from, or a tag like event handler */
    public Object source;
    /** save event parameters */
    public List<String> data;
    /** if mock event handle operation, how long time will take for the operation */
    public long duration;
    /** set this field to true if the event test handle success, will be checked when all test done */
    public boolean verified;
    /** test is it a trigger or not */
    public boolean trigger;
    /** fingerprint point for the test event */
    public String fingerprint;

    private TestEvent generate() {
        id = IdGenerator.getNextId();
        when = Accuracy.now();
        return this;
    }

    private TestEvent clear() {
        id = null;
        when = 0L;
        type = null;
        code = 0;
        consumed = false;
        triggerCause = 0;
        source = null;
        data = null;
        duration = 0L;
        verified = false;
        trigger = false;
        fingerprint = null;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TestEvent event = (TestEvent) o;
        if (code != event.code) {
            return false;
        }
        if (type != null ? !type.equals(event.type) : event.type != null) {
            return false;
        }
        return fingerprint != null ? fingerprint.equals(event.fingerprint) : event.fingerprint == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + code;
        result = 31 * result + (fingerprint != null ? fingerprint.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestEvent{" +
                "id='" + id + '\'' +
                ", when=" + when +
                ", type='" + type + '\'' +
                ", code=" + code +
                ", consumed=" + consumed +
                ", triggerCause=" + triggerCause +
                ", source=" + source +
                ", data=" + data +
                ", duration=" + duration +
                ", verified=" + verified +
                ", trigger=" + trigger +
                ", fingerprint='" + fingerprint + '\'' +
                '}';
    }
}
