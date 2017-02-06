package com.johnsoft.test;

import org.ter.antlr4.TestEvent;

public class VoiceTrigger implements TestUnit {
    public void fromVoice(String type) {
        int code = -1;
        if ("tts".equals(type)) {
            code = 0;
        } else if ("system-light".equals(type)) {
            code = 1;
        } else if ("media".equals(type)) {
            code = 2;
        }
        TestEvent testEvent = TestEvent.obtain();
        testEvent.type = "voice";
        testEvent.code = code;
        testEvent.fingerprint = "VoiceTrigger#fromVoice";
        testEvent.trigger = true;
        TestBridge.recordTriggerEvent(testEvent);
        if ("tts".equals(type)) {
            TtsVerifier.speak();
        } else if ("system-light".equals(type)) {
            SystemDeviceVerifier.changeLightColor();
        } else if ("media".equals(type)) {
            MediaVerifier.playMp3();
        }
    }

    @Override
    public boolean onTestEvent(TestEvent event) {
        switch (event.code) {
            case 0:
                fromVoice("tts");
                break;
            case 1:
                fromVoice("system-light");
                break;
            case 2:
                fromVoice("media");
                break;
        }
        return false;
    }

    @Override
    public String mappingType() {
        return "voice";
    }
}
