package com.ghosttrace.haunt;

import com.ghosttrace.config.Settings;
import com.ghosttrace.ghost.GhostEngine;
import com.ghosttrace.ghost.GhostProfileId;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class HauntEngine {

    private final Settings settings;
    private final GhostEngine ghostEngine;
    private HauntMode mode;
    private final Map<String, Instant> cooldowns = new ConcurrentHashMap<>();
    private final List<String> eventHistory = new ArrayList<>();
    private final Random random = new Random();

    private final List<HauntEvent> catalog = List.of(
        new HauntEvent(
            "idle_whisper",
            EventCategory.IDLE_WHISPER,
            "Terminal Inactivity Whisper",
            Map.of(
                GhostProfileId.FORGOTTEN_MACHINE, List.of(
                    "The cursor hums in silence. The machine waits for your next thought.",
                    "Time flows slowly between keystrokes in these dormant sectors.",
                    "Old logs rustle like dry leaves in the terminal cache."
                ),
                GhostProfileId.PHANTOM_CODER, List.of(
                    "Still thinking? Take your time. Great architectures are forged in quietude.",
                    "Pondering the next function? The terminal prompt is waiting whenever you're ready.",
                    "Quiet intervals... the ideal moment to review algorithm invariants."
                ),
                GhostProfileId.GUARDIAN_SPIRIT, List.of(
                    "A quiet pause in the workflow. Rest is as vital to good code as testing.",
                    "The terminal stands steady. Standing by safely whenever you resume.",
                    "Perimeter secure. All systems nominal during this interval."
                )
            ),
            3,
            60
        ),
        new HauntEvent(
            "late_night",
            EventCategory.LATE_NIGHT_HORROR,
            "Midnight / Witching Hour Manifestation",
            Map.of(
                GhostProfileId.FORGOTTEN_MACHINE, List.of(
                    "The witching hour arrives. Only the glow of phosphor keeps the darkness back.",
                    "Past midnight... memory addresses blur into ghostly apparitions."
                ),
                GhostProfileId.PHANTOM_CODER, List.of(
                    "Late night hacking session! The ghosts are compiling and the caffeine is low.",
                    "Beware late-night commits—the gremlins love unreviewed midnight diffs!"
                ),
                GhostProfileId.GUARDIAN_SPIRIT, List.of(
                    "Fatigue breeds production bugs. Consider committing your changes and resting.",
                    "Midnight vigilance: ensure all critical tests pass before closing your shell."
                )
            ),
            5,
            300
        ),
        new HauntEvent(
            "glitch_manifestation",
            EventCategory.GLITCH_MANIFESTATION,
            "Digital Glitch Artifact",
            Map.of(
                GhostProfileId.FORGOTTEN_MACHINE, List.of(
                    "░▒▓ [SECTOR FLICKER] ▓▒░ Corrupted packets drift across the bus...",
                    ":: [SIGNAL DISTORTION] :: An ancient process tried to transmit through stdout."
                ),
                GhostProfileId.PHANTOM_CODER, List.of(
                    "⚡ [STATIC BLIP] ⚡ Did you see that flicker? A bit flipped somewhere in user space!",
                    "~*~ [GLITCH BURST] ~*~ Digital ghosts are meddling with the terminal buffers."
                ),
                GhostProfileId.GUARDIAN_SPIRIT, List.of(
                    "⚠ [TRANSIENT ANOMALY] ⚠ Signal distortion detected in terminal stream. Verified benign.",
                    "† [FILTER ACTIVE] † Memory scrub completed following transient glitch."
                )
            ),
            4,
            120
        )
    );

    public HauntEngine(Settings settings, GhostEngine ghostEngine) {
        this.settings = settings;
        this.ghostEngine = ghostEngine;
        this.mode = HauntMode.fromString(settings.getHauntMode());
    }

    public HauntMode getMode() {
        return mode;
    }

    public void setMode(HauntMode mode) {
        this.mode = mode;
        this.settings.setHauntMode(mode.getValue());
    }

    public boolean isAtmosphereSuppressed() {
        return mode == HauntMode.FOCUS;
    }

    public String triggerEvent(EventCategory category) {
        if (isAtmosphereSuppressed()) return null;

        HauntEvent matched = catalog.stream()
            .filter(e -> e.category() == category)
            .findFirst()
            .orElse(null);

        if (matched == null) return null;

        Instant lastTriggered = cooldowns.get(matched.id());
        if (lastTriggered != null && Instant.now().isBefore(lastTriggered.plusSeconds(matched.cooldownSeconds()))) {
            return null; // Cooldown active
        }

        cooldowns.put(matched.id(), Instant.now());
        GhostProfileId profileId = ghostEngine.getProfile().id();
        String message = matched.resolveText(profileId);

        String banner = formatAtmosphericBanner(category, message);
        synchronized (eventHistory) {
            eventHistory.add(banner);
        }
        return banner;
    }

    public String checkIdleTrigger() {
        if (mode == HauntMode.NORMAL || mode == HauntMode.FOCUS) return null;

        int hour = LocalTime.now().getHour();
        if ((hour >= 23 || hour < 4) && random.nextDouble() < 0.40) {
            return triggerEvent(EventCategory.LATE_NIGHT_HORROR);
        }
        if (random.nextDouble() < 0.30) {
            return triggerEvent(EventCategory.GLITCH_MANIFESTATION);
        }
        return triggerEvent(EventCategory.IDLE_WHISPER);
    }

    public String formatAtmosphericBanner(EventCategory category, String message) {
        return switch (category) {
            case IDLE_WHISPER -> "  ░▒▓ " + message + " ▓▒░";
            case LATE_NIGHT_HORROR -> "  ☠ [WITCHING HOUR] " + message;
            case GLITCH_MANIFESTATION -> "  " + message;
            default -> "  ✦ " + message;
        };
    }

    public List<String> getEventHistory() {
        synchronized (eventHistory) {
            return new ArrayList<>(eventHistory);
        }
    }
}
