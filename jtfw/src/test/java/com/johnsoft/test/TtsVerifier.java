package com.johnsoft.test;

import org.ter.antlr4.TestEvent;

public class TtsVerifier {
    public static void speak() {
        System.out.println("speak start");
        try {
            Thread.sleep(600L);
        } catch (InterruptedException e) {
        }
        System.out.println("speak end");
        TestEvent testEvent = TestEvent.obtain();
        testEvent.type = "tts";
        testEvent.code = 0;
        testEvent.fingerprint = "TtsVerifier#speak";
        TestBridge.recordVerifierEvent(testEvent);
    }
}
