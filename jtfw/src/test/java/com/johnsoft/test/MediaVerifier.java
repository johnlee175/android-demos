package com.johnsoft.test;

import org.ter.antlr4.TestEvent;

public class MediaVerifier {
    public static void playMp3() {
        System.out.println("playMp3 start");
        try {
//            Thread.sleep(1000L);
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
        }
        System.out.println("playMp3 end");
        TestEvent testEvent = TestEvent.obtain();
        testEvent.type = "media";
        testEvent.code = 0;
        testEvent.fingerprint = "MediaVerifier#playMp3";
        TestBridge.recordVerifierEvent(testEvent);
    }
}
