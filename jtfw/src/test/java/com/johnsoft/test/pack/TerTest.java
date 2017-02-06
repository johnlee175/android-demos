package com.johnsoft.test.pack;

import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import org.junit.*;
import org.ter.antlr4.TerReader;
import org.ter.antlr4.TerStruct;
import org.ter.antlr4.TerWriter;
import org.ter.antlr4.TestEvent;

public class TerTest {
    @Test
    public void testGenTer() throws Exception {
        writeTest(getClass().getResource("/gen.ter").getFile());
        Thread.sleep(2000L);
        readTest(getClass().getResource("/gen.ter").getFile());
    }

    @Test
    public void testTestTer() throws Exception {
        readTest(getClass().getResource("/test.ter").getFile());
    }

    public static void readTest(String terFilePath) throws Exception {
        TerStruct terStruct = TerReader.handle(terFilePath, "UTF-8");
        System.err.println(terStruct.toString());
    }

    public static void writeTest(String terFilePath) throws Exception {
        TerWriter writer = new TerWriter(new File(terFilePath),
                "com.johnsoft.test.pack");
        writer.recordCase("a");
        writer.recordTrigger(genEvent("rec", random(20, true, false), randomNumeric(5), randomNumeric(5)));
        Thread.sleep(200L);
        writer.recordVerifier(genEvent("tts", random(5, true, false)));
        Thread.sleep(500L);
        writer.recordVerifier(genEvent("tts", random(5, true, false)));
        Thread.sleep(300L);
        writer.recordVerifier(genEvent("mp3", randomAlphanumeric(20)));
        writer.recordCase("b");
        writer.recordTrigger(genEvent("press"));
        Thread.sleep(20L);
        writer.recordVerifier(genEvent("light"));
        Thread.sleep(1000L);
        writer.recordVerifier(genEvent("audio"));
        Thread.sleep(4000L);
        writer.recordVerifier(genEvent("tts", random(5, true, false)));
        Thread.sleep(180L);
        writer.recordTrigger(genEvent("rec", random(20, true, false), randomNumeric(5), randomNumeric(5)));
        Thread.sleep(800L);
        writer.recordVerifier(genEvent("screen", randomNumeric(5), randomNumeric(5)));
        writer.recordCase("c");
        writer.recordTrigger(genEvent("rec", random(20, true, false), randomNumeric(5), randomNumeric(5)));
        Thread.sleep(90L);
        writer.recordVerifier(genEvent("mp3", randomAlphanumeric(20)));
        Thread.sleep(2000L);
        writer.close();
    }

    private static final Random rand = new Random();

    private static TestEvent genEvent(String type, String...data) {
        TestEvent event = TestEvent.obtain();
        event.type = type;
        event.code = rand.nextInt(50);
        event.duration = rand.nextInt(10000000);
        event.data = new ArrayList<>();
        for (int i = 0; i < data.length; ++i) {
            event.data.add(data[i]);
        }
        return event;
    }
}
