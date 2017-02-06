package com.johnsoft.test;

import org.ter.antlr4.TestEvent;

public class TouchTrigger implements TestUnit {
    public void fromTouch(String type) {
        int code = -1;
        if ("text".equals(type)) {
            code = 0;
        } else if ("system-settings".equals(type)) {
            code = 1;
        }
        TestEvent testEvent = TestEvent.obtain();
        testEvent.type = "touch";
        testEvent.code = code;
        testEvent.fingerprint = "TouchTrigger#fromTouch";
        testEvent.trigger = true;
        TestBridge.recordTriggerEvent(testEvent);
        if ("text".equals(type)) {
            TextVerifier.showText();
        } else if ("system-settings".equals(type)) {
            SystemDeviceVerifier.modifySettings();
            TtsVerifier.speak();
        }
    }

    @Override
    public boolean onTestEvent(TestEvent event) {
        switch (event.code) {
            case 0:
                fromTouch("text");
                break;
            case 1:
                fromTouch("system-settings");
                break;
        }
        return false;
    }

    @Override
    public String mappingType() {
        return "touch";
    }
}
