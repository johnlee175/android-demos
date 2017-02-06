package com.johnsoft.test;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.junit.*;
import org.ter.antlr4.Accuracy;

public class RecordPlayTest {
    @Test
    public void testRecordPlay() throws Exception {
        File pack = new File(getClass().getResource("/pack.ter").getFile());
        if (pack.exists()) {
            pack.delete();
            try {
                Thread.sleep(400L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        TouchTrigger touchTrigger = new TouchTrigger();
        VoiceTrigger voiceTrigger = new VoiceTrigger();

        TestBridge.recorder().startRecord(pack, "");

        TestBridge.recorder().newCase("base-touch-voice");
        touchTrigger.fromTouch("text");
        voiceTrigger.fromVoice("tts");
        voiceTrigger.fromVoice("media");
        TestBridge.recorder().finishCase();

        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        try {
            Thread.sleep(800L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TestBridge.recorder().newCase("multi-voice");
        voiceTrigger.fromVoice("tts");
        voiceTrigger.fromVoice("tts");
        voiceTrigger.fromVoice("media");
        voiceTrigger.fromVoice("tts");
        TestBridge.recorder().finishCase();

        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        try {
            Thread.sleep(400L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TestBridge.recorder().newCase("for-system");
        touchTrigger.fromTouch("system-settings");
        voiceTrigger.fromVoice("system-light");
        TestBridge.recorder().finishCase();

        TestBridge.recorder().stopRecord();

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        touchTrigger = new TouchTrigger();
        voiceTrigger = new VoiceTrigger();
        TestBridge.player().delayLimit(Accuracy.timeUnit().convert(1200L, TimeUnit.MILLISECONDS));
        TestBridge.player().register(touchTrigger);
        TestBridge.player().register(voiceTrigger);
        TestPlayer.TestResult result = TestBridge.player().play(pack);
        System.out.println("\nfailed cases: " + result.failedCases);
    }
}
