package com.ghosttrace.audio;

import com.ghosttrace.config.Settings;
import com.ghosttrace.haunt.HauntMode;
import java.util.Random;

public class HorrorAudioDirector {

    public enum AudioPriority {
        CRITICAL(1),    // Guardian decisions, security blocks
        IMPORTANT(2),   // Startup, shutdown, notifications, coding success
        ATMOSPHERIC(3), // Keystroke clicks, whispers, glitches, mood shifts
        AMBIENT(4);     // Background loops

        private final int level;
        AudioPriority(int level) { this.level = level; }
        public int getLevel() { return level; }
    }

    private final Settings settings;
    private final AudioManager audioManager;
    private final Random random = new Random();

    public HorrorAudioDirector(Settings settings, AudioManager audioManager) {
        this.settings = settings;
        this.audioManager = audioManager;
    }

    public void dispatch(AudioEventId eventId, AudioPriority priority) {
        if (!settings.isAudioEnabled() || settings.isAudioMuted()) return;

        HauntMode mode = HauntMode.fromString(settings.getHauntMode());

        // Suppression rules:
        // FOCUS mode suppresses ATMOSPHERIC and AMBIENT sounds
        if (mode == HauntMode.FOCUS && (priority == AudioPriority.ATMOSPHERIC || priority == AudioPriority.AMBIENT)) {
            return;
        }

        // In NORMAL mode, reduce atmospheric sound frequency
        if (mode == HauntMode.NORMAL && priority == AudioPriority.ATMOSPHERIC) {
            if (random.nextDouble() > 0.20) {
                return;
            }
        }

        audioManager.playEvent(eventId);
    }

    public void onKeystroke() {
        dispatch(AudioEventId.KEYSTROKE_HAUNT, AudioPriority.ATMOSPHERIC);
    }

    public void onGuardianIntercept() {
        dispatch(AudioEventId.GUARDIAN_INTERCEPT, AudioPriority.CRITICAL);
    }

    public void onGuardianApproved() {
        dispatch(AudioEventId.GUARDIAN_APPROVED, AudioPriority.CRITICAL);
    }

    public void onGuardianBlocked() {
        dispatch(AudioEventId.GUARDIAN_BLOCKED, AudioPriority.CRITICAL);
    }

    public void onGlitch() {
        dispatch(AudioEventId.DIGITAL_GLITCH, AudioPriority.ATMOSPHERIC);
    }

    public void onWhisper() {
        AudioEventId[] whispers = { AudioEventId.SPECTRAL_WHISPER, AudioEventId.EERIE_MURMUR, AudioEventId.CHILLING_BREATH };
        AudioEventId chosen = whispers[random.nextInt(whispers.length)];
        dispatch(chosen, AudioPriority.ATMOSPHERIC);
    }
}
