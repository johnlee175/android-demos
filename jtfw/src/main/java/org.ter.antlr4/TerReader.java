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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-26
 */
public class TerReader extends org.ter.antlr4.TerBaseListener {
    public static TerStruct handle(String filePath, String encoding) throws IOException {
        final org.ter.antlr4.TerLexer lexer = new org.ter.antlr4.TerLexer(new ANTLRFileStream(filePath, encoding));
        lexer.setTokenFactory(new CommonTokenFactory(true));
        final org.ter.antlr4.TerParser parser = new org.ter.antlr4.TerParser(new CommonTokenStream(lexer));
        parser.setBuildParseTree(true);
        final ParseTreeWalker walker = new ParseTreeWalker();
        final TerReader handler = new TerReader();
        walker.walk(handler, parser.file());
        return handler.postProcess();
    }

    static <T> T parseField(String text, Class<T> clazz) {
        if (text == null) {
            return null;
        }
        text = text.trim();
        if (text.charAt(0) == '\"' && text.charAt(text.length() - 1) == '\"') {
            text = text.substring(1, text.length() - 1);
        }
        if (clazz == Boolean.class) {
            return clazz.cast(Boolean.parseBoolean(text));
        } else if (clazz == Integer.class) {
            return clazz.cast(Integer.parseInt(text));
        } else if (clazz == Long.class) {
            return clazz.cast(Long.parseLong(text));
        } else if (clazz == Float.class) {
            return clazz.cast(Float.parseFloat(text));
        } else if (clazz == Double.class) {
            return clazz.cast(Double.parseDouble(text));
        } else if (clazz == String.class) {
            return clazz.cast(text);
        } else if (List.class.isAssignableFrom(clazz)) {
            ArrayList<String> data = new ArrayList<>();
            String[] items = text.split("\\s*,\\s*");
            for (String item : items) {
                data.add(item);
            }
            return clazz.cast(data);
        } else {
            if (clazz == boolean.class || clazz == int.class || clazz == long.class
                    || clazz == float.class || clazz == double.class) {
                throw new IllegalArgumentException("Please use wrapper class instead!");
            } else {
                throw new IllegalArgumentException("Unknown class, no implementation!");
            }
        }
    }

    private final LinkedHashMap<String, LinkedHashMap<TerExeText, TestEvent>> triggerMap
            = new LinkedHashMap<>();
    private final LinkedHashMap<String, LinkedHashMap<TerExeText, ArrayList<TerTestCaseItem>>> verifierMap
            = new LinkedHashMap<>();
    private final ArrayList<String> mainRunIds = new ArrayList<>();

    private String packageName;
    private boolean enterMainRun;
    private TerExeText condition;
    private String id;
    private TestEvent event;
    private ArrayList<TerTestCaseItem> events;

    @Override
    public void enterFile(org.ter.antlr4.TerParser.FileContext ctx) {
        packageName = "";
        enterMainRun = false;
        condition = new TerExeText(packageName, TerExeText.CONDITION_DEFAULT);
    }

    @Override
    public void enterPackageDeclaration(org.ter.antlr4.TerParser.PackageDeclarationContext ctx) {
        packageName = ctx.QUALIFIED_NAME().getText();
    }

    @Override
    public void enterCondition(org.ter.antlr4.TerParser.ConditionContext ctx) {
        condition = new TerExeText(packageName, ctx.EXE_TEXT().getText());
    }

    @Override
    public void exitIfStatement(org.ter.antlr4.TerParser.IfStatementContext ctx) {
        condition = new TerExeText(packageName, TerExeText.CONDITION_DEFAULT);
    }

    @Override
    public void enterTriggerStatement(org.ter.antlr4.TerParser.TriggerStatementContext ctx) {
        id = ctx.triggerId().ID().getText();
    }

    @Override
    public void enterCaseStatement(org.ter.antlr4.TerParser.CaseStatementContext ctx) {
        id = ctx.caseId().ID().getText();
    }

    @Override
    public void enterTrigger(org.ter.antlr4.TerParser.TriggerContext ctx) {
        event = TestEvent.obtain();
    }

    @Override
    public void exitTrigger(org.ter.antlr4.TerParser.TriggerContext ctx) {
        LinkedHashMap<TerExeText, TestEvent> pairs = triggerMap.get(id);
        if (pairs == null) {
            pairs = new LinkedHashMap<>();
            triggerMap.put(id, pairs);
        }
        if (event.data == null) {
            event.data = new ArrayList<>();
        }
        pairs.put(condition, event);
        id = null;
        event = null;
    }

    @Override
    public void enterTestCase(org.ter.antlr4.TerParser.TestCaseContext ctx) {
        events = new ArrayList<>();
    }

    @Override
    public void exitTestCase(org.ter.antlr4.TerParser.TestCaseContext ctx) {
        LinkedHashMap<TerExeText, ArrayList<TerTestCaseItem>> pairs = verifierMap.get(id);
        if (pairs == null) {
            pairs = new LinkedHashMap<>();
            verifierMap.put(id, pairs);
        }
        pairs.put(condition, events);
        id =  null;
        events = null;
    }

    @Override
    public void enterIdRef(org.ter.antlr4.TerParser.IdRefContext ctx) {
        if (enterMainRun) {
            mainRunIds.add(ctx.ID().getText());
        } else {
            events.add(new TestEventRef(ctx.ID().getText()));
        }
    }

    @Override
    public void enterVerifier(org.ter.antlr4.TerParser.VerifierContext ctx) {
        event = TestEvent.obtain();
    }

    @Override
    public void exitVerifier(org.ter.antlr4.TerParser.VerifierContext ctx) {
        if (event.data == null) {
            event.data = new ArrayList<>();
        }
        events.add(event);
        event = null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void enterField(org.ter.antlr4.TerParser.FieldContext ctx) {
        if (event != null) {
            String key = ctx.VARIABLE().getText();
            if ("id".equals(key)) {
                event.id = parseText(ctx, String.class);
            } else if ("type".equals(key)) {
                event.type = parseText(ctx, String.class);
            } else if ("when".equals(key)) {
                event.when = parseText(ctx, Long.class);
            } else if ("code".equals(key)) {
                event.code = parseText(ctx, Integer.class);
            } else if ("duration".equals(key)) {
                event.duration = parseText(ctx, Long.class);
            } else if ("fingerprint".equals(key)) {
                event.fingerprint = parseText(ctx, String.class);
            } else if ("data".equals(key)) {
                event.data = parseText(ctx, List.class);
            } else {
                throw new UnsupportedOperationException("Unknown VARIABLE, not implement!");
            }
        }
    }

    @Override
    public void enterMainRun(org.ter.antlr4.TerParser.MainRunContext ctx) {
        enterMainRun = true;
    }

    public TerStruct postProcess() {
        HashMap<String, TestEvent> simpleTriggerMap = flatConditionMap(triggerMap);
        HashMap<String, ArrayList<TerTestCaseItem>> simpleVerifierMap = flatConditionMap(verifierMap);
        final ArrayList<String> ids = mainRunIds;
        final int size = ids.size();
        checkCrossReference(ids, simpleTriggerMap, simpleVerifierMap, size);

        ArrayList<ArrayList<TestEvent>> queues = new ArrayList<>();
        ArrayList<TestEvent> queue;
        for (int i = 0; i < size; ++i) {
            queue = new ArrayList<>();
            recursiveItems(simpleVerifierMap.get(ids.get(i)), simpleTriggerMap, simpleVerifierMap, queue);
            queues.add(queue);
        }
        // replace alias to id
        HashMap<String, TestEvent> distTriggerMap = new HashMap<>(simpleTriggerMap.size());
        TestEvent event;
        for (String key : simpleTriggerMap.keySet()) {
            event = simpleTriggerMap.get(key);
            distTriggerMap.put(event.id, event);
        }
        checkQueueStartWithTrigger(distTriggerMap, queues, size);
        return new TerStruct(distTriggerMap, queues);
    }

    private void checkCrossReference(ArrayList<String> ids,
                                     HashMap<String, TestEvent> simpleTriggerMap,
                                     HashMap<String, ArrayList<TerTestCaseItem>> simpleVerifierMap,
                                     int size) {
        LinkedHashMap<String, ArrayList<String>> idRefs = new LinkedHashMap<>();
        ArrayList<String> refIds;
        // do prepare
        ArrayList<TerTestCaseItem> items;
        TerTestCaseItem item;
        for (int i = 0; i < size; ++i) {
            final String id = ids.get(i);
            items = simpleVerifierMap.get(id);
            if (items == null) {
                throw new IllegalStateException("Main run line exists non-verifier reference!");
            }
            refIds = new ArrayList<>();
            final int len = items.size();
            for (int j = 0; j < len; ++j) {
                item = items.get(j);
                if (item instanceof TestEventRef) {
                    final String refId = ((TestEventRef)item).refId;
                    if (simpleTriggerMap.get(refId) == null) {
                        refIds.add(refId);
                    }
                }
            }
            idRefs.put(id, refIds);
        }
        // do check
        for (String key : idRefs.keySet()) {
            refIds = idRefs.get(key);
            if (refIds == null) {
                continue;
            }
            final int len = refIds.size();
            for (int j = 0; j < len; ++j) {
                final String correspondingKey = refIds.get(j);
                refIds = idRefs.get(correspondingKey);
                if (refIds != null && refIds.contains(key)) {
                    throw new IllegalStateException("Cross reference error: "
                            + key + " and " + correspondingKey  + "!");
                }
            }
        }
    }

    private void checkQueueStartWithTrigger(HashMap<String, TestEvent> distTriggerMap,
                                            ArrayList<ArrayList<TestEvent>> queues, int size) {
        // check test case
        if (size != queues.size()) {
            throw new IllegalStateException("Internal error assert!");
        }
        TestEvent event;
        ArrayList<TestEvent> testCase;
        for (int i = 0; i < size; ++i) {
            testCase = queues.get(i);
            event = testCase.get(0);
            if (event != null) {
                if (distTriggerMap.get(event.id)==null){
                    throw new IllegalStateException("Test case must be start with a trigger event!");
                }
            }
        }
    }

    private <T> T parseText(org.ter.antlr4.TerParser.FieldContext ctx, Class<T> clazz) {
        if (ctx.TEXT() != null) {
            String text = ctx.TEXT().getText();
            return parseField(text, clazz);
        } else if (ctx.EXE_TEXT() != null) {
            return new TerExeText(packageName, ctx.EXE_TEXT().getText()).execute(clazz, false);
        } else {
            throw new IllegalArgumentException("Field value TEXT or EXE_TEXT should be set!");
        }
    }

    private void recursiveItems(ArrayList<TerTestCaseItem> items,
                                HashMap<String, TestEvent> simpleTriggerMap,
                                HashMap<String, ArrayList<TerTestCaseItem>> simpleVerifierMap,
                                ArrayList<TestEvent> queue) {
        final int size = items.size();
        TerTestCaseItem item;
        for (int i = 0; i < size; ++i) {
            item = items.get(i);
            if (item instanceof TestEventRef) {
                TestEventRef ref = (TestEventRef)item;
                TestEvent evt = simpleTriggerMap.get(ref.refId);
                if (evt != null) {
                    queue.add(evt);
                } else {
                    ArrayList<TerTestCaseItem> evts = simpleVerifierMap.get(ref.refId);
                    if (evts != null) {
                        recursiveItems(evts, simpleTriggerMap, simpleVerifierMap, queue);
                    } else {
                        throw new IllegalStateException("Unknown ref to trigger or verifier case definition!");
                    }
                }
            } else if (item instanceof TestEvent) {
                queue.add((TestEvent)item);
            }
        }
    }

    private <T> HashMap<String, T> flatConditionMap(LinkedHashMap<String, LinkedHashMap<TerExeText, T>> m) {
        final HashMap<String, T> simpleMap = new HashMap<>();
        LinkedHashMap<TerExeText, T> innerMap;
        for (String key : m.keySet()) {
            innerMap = m.get(key);
            for (TerExeText innerKey : innerMap.keySet()) {
                if (innerKey.execute(Boolean.class, true)) {
                    simpleMap.put(key, innerMap.get(innerKey));
                    break;
                }
            }
        }
        return simpleMap;
    }

    static class TerExeText {
        static final String CONDITION_DEFAULT = "@\"default\"";

        final String packageName;
        final String originText;

        TerExeText(String packageName, String originText) {
            this.packageName = packageName;
            this.originText = originText.trim();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TerExeText that = (TerExeText) o;
            if (packageName != null ? !packageName.equals(that.packageName) : that.packageName != null) {
                return false;
            }
            return originText != null ? originText.equals(that.originText) : that.originText == null;

        }

        @Override
        public int hashCode() {
            int result = packageName != null ? packageName.hashCode() : 0;
            result = 31 * result + (originText != null ? originText.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return originText;
        }

        <T> T execute(Class<T> klass, boolean isCondition) {
            if (isCondition && CONDITION_DEFAULT.equals(originText)) {
                return klass.cast(Boolean.TRUE);
            }
            if (!originText.startsWith("@\"") || !originText.endsWith("()\"")) {
                throw new IllegalArgumentException("Invalid format of EXE_TEXT!");
            }
            final String sign = packageName + '.' + originText.substring(2, originText.length() - 3);
            final int lastDotIdx = sign.lastIndexOf('.');
            final String className = sign.substring(0, lastDotIdx);
            final String methodName = sign.substring(lastDotIdx + 1);
            try {
                final Class<?> clazz = Class.forName(className);
                final Method method = clazz.getMethod(methodName);
                final Object object = method.invoke(null);
                if (isCondition && (object == null || !(object instanceof Boolean))) {
                    throw new IllegalArgumentException("Condition implementation not return boolean for EXE_TEXT!");
                }
                return klass.cast(object);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Invalid class name of EXE_TEXT!");
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Invalid method name of EXE_TEXT!");
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Method modifier should be 'public static' in EXE_TEXT!");
            } catch (InvocationTargetException e) {
                throw new IllegalArgumentException("Method call failed in EXE_TEXT, has return value?");
            }
        }
    }
}
