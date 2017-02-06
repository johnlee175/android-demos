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

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Simple Delayed object
 * @author John Kenrinus Lee
 * @version 2016-12-28
 */
public class DelayedItemX<T> implements Delayed {
    private final long delay;
    private final BaseTimeLazyEvaluator evaluator;
    public final T item;

    /**
     * @param t portable composite object
     * @param delay the delay time with no base
     * @param evaluator the base time lazy evaluator
     */
    public DelayedItemX(T t, long delay, BaseTimeLazyEvaluator evaluator) {
        this.item = t;
        this.delay = delay;
        this.evaluator = evaluator;
        if (evaluator == null) {
            throw new IllegalArgumentException("evaluator(BaseTimeLazyEvaluator) is null");
        }
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(evaluator.getBaseTimeHolding() + delay - Accuracy.now(),
                Accuracy.timeUnit());
    }

    @Override
    public int compareTo(Delayed o) {
        if (o == this) {
            return 0;
        }
        if (o instanceof DelayedItemX) {
            if (this.delay < ((DelayedItemX) o).delay) {
                return -1;
            } else if (this.delay > ((DelayedItemX) o).delay) {
                return 1;
            } else {
                return 0;
            }
        }
        throw new UnsupportedOperationException("Unable to compare with mismatched type!");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DelayedItemX<?> that = (DelayedItemX<?>) o;
        return evaluator == that.evaluator && (item != null ? item.equals(that.item) : that.item == null);
    }

    @Override
    public int hashCode() {
        int result = evaluator.hashCode();
        result = 31 * result + (item != null ? item.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DelayedItemX{" +
                "delay=" + delay +
                ", evaluator=" + evaluator +
                ", item=" + item +
                '}';
    }

    public interface BaseTimeLazyEvaluator  {
        long getBaseTimeHolding();
    }

    public static final class BaseTimeHolder implements BaseTimeLazyEvaluator {
        private long baseTime;

        public void setBaseTime(long baseTime) {
            this.baseTime = baseTime;
        }

        @Override
        public long getBaseTimeHolding() {
            return baseTime;
        }
    }
}
