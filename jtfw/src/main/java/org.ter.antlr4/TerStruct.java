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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-27
 */
public class TerStruct {
    public final HashMap<String, TestEvent> triggerMap;
    public final ArrayList<ArrayList<TestEvent>> testCases;

    public TerStruct(HashMap<String, TestEvent> triggerMap, ArrayList<ArrayList<TestEvent>> testCases) {
        if (triggerMap != null) {
            this.triggerMap = triggerMap;
        } else {
            this.triggerMap = new HashMap<>();
        }
        if (testCases != null) {
            this.testCases = testCases;
        } else {
            this.testCases = new ArrayList<>();
        }
    }

    @Override
    public String toString() {
        return "TerStruct{" +
                "triggerMap=" + triggerMap +
                ", testCases=" + testCases +
                '}';
    }
}
