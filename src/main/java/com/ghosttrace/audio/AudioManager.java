package com.ghosttrace.audio;

import com.ghosttrace.config.Settings;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

public class AudioManager {

    private final Settings settings;
    private final ExecutorService audioExecutor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "ghosttrace-audio-player");
        t.setDaemon(true);
        return t;
    });

    private volatile boolean isAvailable = true;

    public AudioManager(Settings settings) {
        this.settings = settings;
        checkAvailability();
    }

    private void checkAvailability() {
        try {
            AudioSystem.getMixerInfo();
            this.isAvailable = true;
        } catch (Throwable e) {
            this.isAvailable = false;
        }
    }

    public boolean isAvailable() {
        return isAvailable && settings.isAudioEnabled();
    }

    public boolean isMuted() {
        return settings.isAudioMuted();
    }

    public void setMuted(boolean muted) {
        settings.setAudioMuted(muted);
        settings.save();
    }

    public float getMasterVolume() {
        return settings.getAudioMasterVolume();
    }

    public void setMasterVolume(float volume) {
        settings.setAudioMasterVolume(volume);
        settings.save();
    }

    public void playEvent(AudioEventId eventId) {
        if (!isAvailable() || isMuted()) return;

        audioExecutor.submit(() -> {
            try {
                AudioInputStream ais = loadAudioStream(eventId);
                if (ais == null) return;

                AudioFormat baseFormat = ais.getFormat();
                AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
                );

                AudioInputStream dais = AudioSystem.getAudioInputStream(decodedFormat, ais);
                DataLine.Info info = new DataLine.Info(Clip.class, decodedFormat);
                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.open(dais);

                // Set Volume via MASTER_GAIN control
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    float volume = settings.getAudioMasterVolume();
                    float dB = (float) (Math.log10(Math.max(0.0001f, volume)) * 20.0);
                    float clampedDB = Math.clamp(dB, gainControl.getMinimum(), gainControl.getMaximum());
                    gainControl.setValue(clampedDB);
                }

                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });

                clip.start();
            } catch (Throwable t) {
                // Audio hardware or decoding error ignored gracefully
            }
        });
    }

    private AudioInputStream loadAudioStream(AudioEventId eventId) {
        try {
            // 1. Try loading from resources
            String resourcePath = "/assets/audio/" + eventId.getRelativePath();
            InputStream in = getClass().getResourceAsStream(resourcePath);
            if (in != null) {
                return AudioSystem.getAudioInputStream(new BufferedInputStream(in));
            }
        } catch (Exception ignored) {}

        // 2. Fall back to pure Java DSP synthesis
        try {
            return AudioSynthesizer.synthesize(eventId);
        } catch (Exception ignored) {
            return null;
        }
    }

    public void playTestSound() {
        playEvent(AudioEventId.NOTIFICATION_PING);
    }

    public void shutdown() {
        audioExecutor.shutdown();
        try {
            audioExecutor.awaitTermination(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {}
    }
}
