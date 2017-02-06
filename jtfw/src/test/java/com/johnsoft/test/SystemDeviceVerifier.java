package com.johnsoft.test;

import org.ter.antlr4.TestEvent;

public class SystemDeviceVerifier {
    public static void modifySettings() {
        System.out.println("modifySettings start");
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
        }
        System.out.println("modifySettings end");
        TestEvent testEvent = TestEvent.obtain();
        testEvent.type = "system";
        testEvent.code = 0;
        testEvent.fingerprint = "SystemDeviceVerifier#modifySettings";
        TestBridge.recordVerifierEvent(testEvent);
    }

    public static void changeLightColor() {
        System.out.println("changeLightColor");
        TestEvent testEvent = TestEvent.obtain();
        testEvent.type = "system";
        testEvent.code = 1;
        testEvent.fingerprint = "SystemDeviceVerifier#changeLightColor";
        TestBridge.recordVerifierEvent(testEvent);
    }
}
