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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.ter.antlr4.Accuracy;
import org.ter.antlr4.DelayedItemX;
import org.ter.antlr4.TerReader;
import org.ter.antlr4.TerStruct;
import org.ter.antlr4.TestEvent;

/**
 * Thread safe.
 *
 * @author John Kenrinus Lee
 * @version 2016-12-29
 */
public class TestPlayerImpl implements TestPlayer {
    private final Lock playMutexLock = new ReentrantLock(false);
    private final Lock stopMutexLock = new ReentrantLock(false);
    private final Lock delayLimitMutexLock = new ReentrantLock(false);
    private final ConcurrentHashMap<String, TestUnit> typeUnitMap = new ConcurrentHashMap<>();
    private final DelayQueue<DelayedItemX<TestEvent>> currentDelayQueue = new DelayQueue<>();
    private volatile Thread playThread;
    private volatile boolean playing; // only play method modify it with mutex
    private volatile boolean stop; // only stop method modify it with mutex
    private volatile long delayLimit; // only delayLimit method modify it with mutex

    @Override
    public void register(TestUnit unit) {
        typeUnitMap.put(unit.mappingType(), unit);
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    /** Play on the current thread. */
    @Override
    public TestResult play(File file) {
        playMutexLock.lock();
        try {
            LinkedHashMap<Integer, TestEvent> failedCases = new LinkedHashMap<>();
            playThread = Thread.currentThread();
            playing = true;

            TerStruct struct = TerReader.handle(file.getAbsolutePath(), "UTF-8");
            if (stop) {
                return null;
            }
            ArrayList<ArrayList<TestEvent>> testCases = struct.testCases;
            HashMap<String, TestEvent> triggerMap = struct.triggerMap;
            DelayedItemX.BaseTimeHolder evaluator = new DelayedItemX.BaseTimeHolder();
            ArrayList<TestEvent> testEvents;
            final int tcs = testCases.size();
            for (int i = 0; i < tcs; ++i) {
                testEvents = testCases.get(i);
                ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
                try {
                    if (stop) {
                        return null;
                    }
                    currentDelayQueue.clear();
                    final int tes = testEvents.size();
                    TestEvent testEvent;
                    String delays = Accuracy.now() + "\tplay>>> delay: ";
                    for (int j = 0; j < tes; ++j) {
                        testEvent = testEvents.get(j);
                        testEvent.trigger = triggerMap.containsKey(testEvent.id);
                        long delay = testEvent.when + delayLimit * (j + 1);
                        delays += delay + "; ";
                        currentDelayQueue.offer(new DelayedItemX<>(testEvent, delay, evaluator));
                    }
                    System.out.println(delays);
                    long baseTime = Accuracy.now();
                    System.out.println(Accuracy.now() + "\tplay>>> base time: " + baseTime + ", current case: " + i);
                    evaluator.setBaseTime(baseTime);
                    try {
                        TestUnit testUnit;
                        while (!currentDelayQueue.isEmpty()) {
                            if (stop) {
                                return null;
                            }
                            testEvent = currentDelayQueue.take().item;
                            System.out.println(Accuracy.now() + "\tplay>>> take from delay queue: " + testEvent);
                            if (testEvent.trigger) {
                                testUnit = typeUnitMap.get(testEvent.type);
                                if (testUnit != null) {
                                    singleThreadExecutor.execute(new OnTestEventRunnable(testUnit, testEvent));
                                } else {
                                    throw new IllegalStateException("Unregister unit type!");
                                }
                            } else {
                                System.out.println(Accuracy.now() + "\tplay>>> this case failed");
                                failedCases.put(i, testEvent);
                                currentDelayQueue.clear();
                            }
                        }
                    } catch (InterruptedException e) {
                        // ignore it here
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                singleThreadExecutor.shutdownNow();
            }
            return new TestResult(struct, failedCases);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            currentDelayQueue.clear();
            playing = false;
            try {
                Thread.sleep(5L);
            } catch (InterruptedException e) {
                // ignore it here
            }
            playMutexLock.unlock();
        }
    }

    @Override
    public void stop() {
        stopMutexLock.lock();
        try {
            while (isPlaying()) {
                stop = true;
                Thread.sleep(5L);
            }
        } catch (InterruptedException e) {
            // ignore it here
        } finally {
            stop = false;
            stopMutexLock.unlock();
        }
    }

    @Override
    public long delayLimit(long delayLimit) {
        delayLimitMutexLock.lock();
        try {
            long oldOne = this.delayLimit;
            this.delayLimit = delayLimit;
            return oldOne;
        } finally {
            delayLimitMutexLock.unlock();
        }
    }

    @Override
    public void verify(TestEvent event) {
        DelayedItemX<TestEvent> itemX = currentDelayQueue.peek();
        System.out.println(Accuracy.now() + "\tverify>>> peek: " + itemX + ", param: " + event);
        if (itemX != null) {
            if (itemX.item.equals(event)) {
                System.out.println(Accuracy.now() + "\tverify>>> remove " + itemX);
                currentDelayQueue.remove(itemX);
                if (currentDelayQueue.isEmpty() && playThread != null && playThread.isAlive()) {
                    playThread.interrupt();
                }
            }
        }
    }

    private static final class OnTestEventRunnable implements Runnable {
        private final TestUnit unit;
        private final TestEvent event;

        private OnTestEventRunnable(TestUnit unit, TestEvent event) {
            this.unit = unit;
            this.event = event;
        }

        @Override
        public void run() {
            unit.onTestEvent(event);
        }
    }
}
