package com.ghosttrace.audio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * Pure Java DSP procedural audio generator.
 * Synthesizes 16-bit 44.1kHz stereo PCM WAV data in memory for horror effects.
 */
public class AudioSynthesizer {

    public static final float SAMPLE_RATE = 44100.0f;
    private static final Random RANDOM = new Random(42);

    public static AudioInputStream synthesize(AudioEventId eventId) {
        float duration = switch (eventId) {
            case STARTUP -> 1.2f;
            case SHUTDOWN -> 0.8f;
            case KEYSTROKE_HAUNT -> 0.08f;
            case NOTIFICATION_PING, NOTIFICATION_ALERT -> 0.35f;
            case STATIC_BURST, DIGITAL_GLITCH -> 0.25f;
            case DARK_HUM, COLD_WIND, DISTANT_THUNDER -> 2.5f;
            default -> 0.6f;
        };

        int totalSamples = (int) (duration * SAMPLE_RATE);
        byte[] pcmData = new byte[totalSamples * 4]; // 16-bit stereo = 4 bytes per frame
        ByteBuffer buffer = ByteBuffer.wrap(pcmData).order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < totalSamples; i++) {
            float t = i / SAMPLE_RATE;
            float left = 0.0f;
            float right = 0.0f;

            switch (eventId) {
                case STARTUP -> {
                    // Ascending resonant sine chords with shimmer
                    float f1 = 220.0f + 110.0f * (t / duration);
                    float f2 = 330.0f + 165.0f * (t / duration);
                    float env = (float) Math.sin(Math.PI * (t / duration));
                    left = (float) (Math.sin(2 * Math.PI * f1 * t) * 0.4f * env);
                    right = (float) (Math.sin(2 * Math.PI * f2 * t) * 0.4f * env);
                }
                case SHUTDOWN -> {
                    // Descending sine into low rumble
                    float f = 200.0f * (1.0f - t / duration);
                    float env = (float) Math.exp(-3.0f * t);
                    left = (float) (Math.sin(2 * Math.PI * f * t) * 0.5f * env);
                    right = left;
                }
                case NOTIFICATION_PING, GUARDIAN_APPROVED -> {
                    // Crisp chime ping at 880Hz + 1320Hz harmonic
                    float env = (float) Math.exp(-8.0f * t);
                    left = (float) ((Math.sin(2 * Math.PI * 880.0 * t) + 0.5 * Math.sin(2 * Math.PI * 1320.0 * t)) * 0.35f * env);
                    right = left;
                }
                case NOTIFICATION_ALERT, GUARDIAN_BLOCKED -> {
                    // Low dissonant alert buzz (140Hz + 196Hz)
                    float env = (float) Math.exp(-5.0f * t);
                    left = (float) ((Math.sin(2 * Math.PI * 140.0 * t) + Math.sin(2 * Math.PI * 196.0 * t)) * 0.45f * env);
                    right = left;
                }
                case STATIC_BURST, DIGITAL_GLITCH, CORRUPTION_ARTIFACT -> {
                    // High-frequency noise glitch burst
                    float env = (float) Math.exp(-12.0f * t);
                    left = (RANDOM.nextFloat() * 2.0f - 1.0f) * 0.35f * env;
                    right = (RANDOM.nextFloat() * 2.0f - 1.0f) * 0.35f * env;
                }
                case KEYSTROKE_HAUNT -> {
                    // Very short sharp click + resonant sub-pulse
                    float env = (float) Math.exp(-40.0f * t);
                    left = (float) (Math.sin(2 * Math.PI * 600.0 * t) * 0.3f * env) + (RANDOM.nextFloat() - 0.5f) * 0.1f * env;
                    right = left;
                }
                case DARK_HUM, COLD_WIND, DISTANT_THUNDER -> {
                    // Low eerie ambient sub-bass drone
                    float env = (float) Math.sin(Math.PI * (t / duration));
                    float sub = (float) Math.sin(2 * Math.PI * 55.0 * t);
                    float detune = (float) Math.sin(2 * Math.PI * 57.5 * t);
                    float noise = (RANDOM.nextFloat() - 0.5f) * 0.08f;
                    left = (sub * 0.3f + noise) * env;
                    right = (detune * 0.3f + noise) * env;
                }
                default -> {
                    // Spectral murmur whisper
                    float env = (float) Math.sin(Math.PI * (t / duration));
                    float whisper = (RANDOM.nextFloat() - 0.5f) * 0.25f * env;
                    left = whisper;
                    right = whisper;
                }
            }

            short sLeft = (short) Math.clamp((int) (left * 32767.0f), -32768, 32767);
            short sRight = (short) Math.clamp((int) (right * 32767.0f), -32768, 32767);
            buffer.putShort(sLeft);
            buffer.putShort(sRight);
        }

        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
        return new AudioInputStream(new ByteArrayInputStream(pcmData), format, totalSamples);
    }
}
