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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-27
 */
public class TerWriter implements Closeable {
    private final File outputPath;
    private final String packageName;
    private final RandomAccessFile writer;
    private final ArrayList<String> mainRuns = new ArrayList<>();
    private final LinkedHashMap<String, TestEvent> triggerMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, ArrayList<TerTestCaseItem>> caseMap = new LinkedHashMap<>();
    private ArrayList<TerTestCaseItem> currentCase;
    private boolean isClosed;
    private long base;

    // config and start main run queue
    public TerWriter(File outputPath, String packageName) {
        if (outputPath == null) {
            throw new IllegalArgumentException("outputPath == null");
        }
        this.outputPath = outputPath;
        if (packageName == null) {
            packageName = "";
        }
        this.packageName = packageName.trim();
        try {
            boolean deleteSuccess = true;
            if (outputPath.exists()) {
                if (!(deleteSuccess = outputPath.delete())) {
                    System.out.println("Notice: delete old " + outputPath + " failed! ");
                }
            }
            this.writer = new RandomAccessFile(this.outputPath, "rws");
            if (!deleteSuccess) {
                writer.seek(0);
                writer.setLength(0);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        isClosed = false;
    }

    // for save to storage
    public synchronized void close() throws IOException {
        if (isClosed) {
            throw new IllegalStateException("state is closed!");
        }
        isClosed = true;
        final StringBuilder sb = new StringBuilder(512);
        writePackage(sb);
        writer.write(sb.toString().getBytes("UTF-8"));
        sb.setLength(0);
        writeTriggers(sb);
        writer.write(sb.toString().getBytes("UTF-8"));
        sb.setLength(0);
        writeCases(sb);
        writer.write(sb.toString().getBytes("UTF-8"));
        sb.setLength(0);
        writeMainRun(sb);
        writer.write(sb.toString().getBytes("UTF-8"));
    }

    // start case define, and push to main run queue
    public synchronized void recordCase(String mark /* use for next version */) {
        if (isClosed) {
            throw new IllegalStateException("state is closed!");
        }
        final String id = IdGenerator.getNextId();
        currentCase = new ArrayList<>();
        caseMap.put(id, currentCase);
        mainRuns.add(id);
        base = Accuracy.now();
    }

    // start trigger define, and push to current case
    public synchronized void recordTrigger(TestEvent testEvent) {
        if (isClosed) {
            throw new IllegalStateException("state is closed!");
        }
        testEvent.when -= base;
        final String id = IdGenerator.getNextId();
        triggerMap.put(id, testEvent);
        currentCase.add(new TestEventRef(id));
    }

    // write verifier to current case
    public synchronized void recordVerifier(TestEvent testEvent) {
        if (isClosed) {
            throw new IllegalStateException("state is closed!");
        }
        testEvent.when -= base;
        currentCase.add(testEvent);
    }

    private void writePackage(StringBuilder sb) throws IOException {
        final String packageName = this.packageName;
        if (!"".equals(packageName)) {
            sb.append("package ").append(packageName).append(";\n\n");
        }
    }

    private void writeTriggers(StringBuilder sb) throws IOException {
        final LinkedHashMap<String, TestEvent> triggerMap = this.triggerMap;
        for (String id : triggerMap.keySet()) {
            sb.append(id).append(" = ");
            writeEvent(sb, triggerMap.get(id), false, 1);
            sb.append(";\n");
        }
    }

    private void writeCases(StringBuilder sb) throws IOException {
        final LinkedHashMap<String, ArrayList<TerTestCaseItem>> caseMap = this.caseMap;
        ArrayList<TerTestCaseItem> caseItems;
        TerTestCaseItem terTestCaseItem;
        for (String id : caseMap.keySet()) {
            sb.append(id).append(" = [\n");
            caseItems = caseMap.get(id);
            final int size = caseItems.size();
            for (int i = 0; i < size; i++) {
                terTestCaseItem = caseItems.get(i);
                if (terTestCaseItem instanceof TestEventRef) {
                    sb.append(getIndents(1)).append("@").append(((TestEventRef) terTestCaseItem).refId);
                } else if (terTestCaseItem instanceof TestEvent) {
                    writeEvent(sb, ((TestEvent) terTestCaseItem), false, 2);
                } else {
                    throw new UnsupportedOperationException("Stub: Not implement!");
                }
                if (i < size - 1) {
                    sb.append(", ");
                }
            }
            sb.append("\n];\n");
        }
    }

    private void writeMainRun(StringBuilder sb) throws IOException {
        final ArrayList<String> mainRuns = this.mainRuns;
        sb.append("\nmain:\n");
        final int size = mainRuns.size();
        for (int i = 0; i < size; ++i) {
            if (i > 0) {
                sb.append("=>");
            }
            sb.append(" @").append(mainRuns.get(i)).append(" ");
        }
        sb.append(";\n\n");
    }

    private void writeEvent(StringBuilder sb, TestEvent event, boolean writeId, int indents) throws IOException {
        sb.append(getIndents(indents - 1)).append("{\n");
        if (writeId) {
            sb.append(getIndents(indents)).append("id = \"").append(event.id).append("\";\n");
        }
        sb.append(getIndents(indents)).append("when = \"").append(event.when).append("\";\n");
        sb.append(getIndents(indents)).append("duration = \"").append(event.duration).append("\";\n");
        sb.append(getIndents(indents)).append("type = \"").append(event.type).append("\";\n");
        sb.append(getIndents(indents)).append("code = \"").append(event.code).append("\";\n");
        sb.append(getIndents(indents)).append("fingerprint = \"").append(event.fingerprint).append("\";\n");
        final List<String> data = event.data;
        if (data != null && !data.isEmpty()) {
            sb.append(getIndents(indents)).append("data = \"");
            final int size = data.size();
            for (int i = 0; i < size; ++i) {
                sb.append(data.get(i));
                if (i < size - 1) {
                    sb.append(", ");
                }
            }
            sb.append("\";\n");
        }
        sb.append("\n").append(getIndents(indents - 1)).append("}");
    }

    private String getIndents(int indents) {
        String result = "";
        for (int i = 0; i < indents; ++i) {
            result += '\t';
        }
        return result;
    }
}
