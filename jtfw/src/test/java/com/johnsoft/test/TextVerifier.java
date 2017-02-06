package com.johnsoft.test;

import org.ter.antlr4.TestEvent;

public class TextVerifier {
    public static void showText() {
        System.out.println("showText");
        TestEvent testEvent = TestEvent.obtain();
        testEvent.type = "text";
        testEvent.code = 0;
        testEvent.fingerprint = "TextVerifier#showText";
        TestBridge.recordVerifierEvent(testEvent);
    }
}
