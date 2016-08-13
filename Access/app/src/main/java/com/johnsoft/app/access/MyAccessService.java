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
package com.johnsoft.app.access;

import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * @author John Kenrinus Lee
 * @version 2016-08-12
 */
public class MyAccessService extends AccessibilityService {
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        System.out.println("^^^^^^^^" + event);
        if ("android".equals(event.getPackageName())) {
            if ("com.android.server.am.AppNotRespondingDialog".equals(event.getClassName())) {
                if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event.getEventType()) {
                    final AccessibilityNodeInfo source = event.getSource();
                    System.out.println("Handle ANR! " + source);
                    // System.out.println(event.getText()); // JUST TEST IT
                    final String text = event.getText().get(2).toString(); // 1: CANCEL; 2: OK;
                    findNodesByTextAndPerformAll(source, text);
                }
            }
        }

        if ("com.android.settings".equals(event.getPackageName())) {
            if ("com.android.settings.applications.InstalledAppDetailsTop".equals(event.getClassName())) {
                if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event.getEventType()) {
                    final AccessibilityNodeInfo source = event.getSource();
                    System.out.println("Handle package force-stop! " + source);
                    String[] ok = new String[] {"FORCE STOP", "强行停止"};
                    for (String forceStop : ok) {
                        if (findNodesByTextAndPerformAll(source, forceStop)) {
                            break;
                        }
                    }
                }
            } else if ("android.app.AlertDialog".equals(event.getClassName())) {
                if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event.getEventType()) {
                    final List<CharSequence> texts = event.getText();
                    if (texts != null && texts.size() >= 4 && texts.get(0) != null
                            && (texts.get(0).toString().toLowerCase().contains("force stop")
                                || texts.get(0).toString().contains("强行停止"))) {
                        final AccessibilityNodeInfo source = event.getSource();
                        System.out.println("Handle package force-stop finish! " + source);
                        // System.out.println(event.getText()); // JUST TEST IT
                        final String text = event.getText().get(3).toString(); // 2: CANCEL; 3: OK;
                        findNodesByTextAndPerformAll(source, text);
                        if (Build.VERSION.SDK_INT >= 16) {
                            mainHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    performBack();
                                }
                            }, 500L);
                        }
                    }
                }
            }
        }

        if ("com.android.packageinstaller".equals(event.getPackageName())) {
            if ("com.android.packageinstaller.PackageInstallerActivity".equals(event.getClassName())) {
                if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event.getEventType()) {
                    final AccessibilityNodeInfo source = event.getSource();
                    System.out.println("Handle package install! " + source);
                    String[] ok = new String[] {"INSTALL", "安装"};
                    for (String install : ok) {
                        if (findNodesByTextAndPerformAll(source, install)) {
                            break;
                        }
                    }
                }
            } else if ("android.app.AlertDialog".equals(event.getClassName())) {
                if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event.getEventType()) {
                    final List<CharSequence> texts = event.getText();
                    if (texts != null && texts.size() >= 4 && texts.get(1) != null
                            && (texts.get(1).toString().toLowerCase().contains("uninstall")
                                        || texts.get(1).toString().contains("卸载"))) {
                        final AccessibilityNodeInfo source = event.getSource();
                        System.out.println("Handle Uninstall! " + source);
                        // System.out.println(event.getText()); // JUST TEST IT
                        final String text = event.getText().get(3).toString(); // 2: CANCEL; 3: OK;
                        findNodesByTextAndPerformAll(source, text);
                    }
                }
            } else if ("com.android.packageinstaller.InstallAppProgress".equals(event.getClassName())) {
                if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event.getEventType()) {
                    final AccessibilityNodeInfo source = event.getSource();
                    if (source == null) {
                        return;
                    }
                    System.out.println("Handle package install finish! " + source);
                    String[] open = new String[] {"OPEN", "打开"};
                    String[] done = new String[] {"DONE", "完成"};
                    boolean handled = false;
                    for (String item : open) {
                        if (findNodesByTextAndPerformAll(source, item)) {
                            handled = true;
                            break;
                        }
                    }
                    if (!handled && Build.VERSION.SDK_INT >= 16) {
                        performBack();
                    }
                }
            }
        }

    }

    @Override
    public void onInterrupt() {
        System.err.println("onInterrupt called! OH NO OH NO OH NO OH NO OH NO OH NO OH NO OH NO");
    }

    private boolean findNodesByTextAndPerformAll(AccessibilityNodeInfo source, String text) {
        List<AccessibilityNodeInfo> infos = source.findAccessibilityNodeInfosByText(text);
        if (infos != null && !infos.isEmpty()) {
            for (AccessibilityNodeInfo info : infos) {
                performClick(info);
            }
            return true;
        } else {
            System.out.println("Can't find accessibility node info by text: " + text);
            return false;
        }
    }

    private void performClick(AccessibilityNodeInfo node) {
        if (node != null && node.getClassName().equals("android.widget.Button")
                && node.isEnabled() && node.isClickable()) {
            if (node.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                System.out.println(node.getText() + " performClick SUCCESSFUL!");
            } else {
                System.err.println(node.getText() + " performClick FAILED!");
            }
        }
    }

    @TargetApi(16)
    private void performBack() {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        System.out.println("performBack!!!");
    }
}
