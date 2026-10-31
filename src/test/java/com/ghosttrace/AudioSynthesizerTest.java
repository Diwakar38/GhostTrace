package com.ghosttrace;

import com.ghosttrace.audio.AudioEventId;
import com.ghosttrace.audio.AudioSynthesizer;
import javax.sound.sampled.AudioInputStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AudioSynthesizerTest {

    @Test
    void shouldSynthesizeValidPcmWavStreams() throws Exception {
        AudioInputStream ais = AudioSynthesizer.synthesize(AudioEventId.STARTUP);
        assertNotNull(ais);
        assertEquals(44100.0f, ais.getFormat().getSampleRate());
        assertEquals(16, ais.getFormat().getSampleSizeInBits());
        assertEquals(2, ais.getFormat().getChannels());

        byte[] buffer = new byte[1024];
        int read = ais.read(buffer);
        assertTrue(read > 0);
    }

    @Test
    void shouldSynthesizeKeystrokeAndPingEffects() throws Exception {
        AudioInputStream keyClick = AudioSynthesizer.synthesize(AudioEventId.KEYSTROKE_HAUNT);
        assertNotNull(keyClick);
        assertTrue(keyClick.getFrameLength() > 0);

        AudioInputStream ping = AudioSynthesizer.synthesize(AudioEventId.NOTIFICATION_PING);
        assertNotNull(ping);
        assertTrue(ping.getFrameLength() > 0);
    }
}
