package com.ghosttrace.ghost;

import com.ghosttrace.config.Settings;
import java.time.LocalTime;
import java.util.Random;

public class GhostEngine {

    private final Settings settings;
    private PersonalityProfile profile;
    private GhostMood mood = GhostMood.NEUTRAL;
    private final Random random = new Random();

    private static final String[] GLITCH_GLYPHS = { "░", "▒", "▓", "†", "✦", "☠", "::", ">>" };

    public GhostEngine(Settings settings) {
        this.settings = settings;
        setProfile(settings.getGhostProfile());
        checkTimeBasedMood();
    }

    public PersonalityProfile getProfile() {
        return profile;
    }

    public void setProfile(String profileName) {
        GhostProfileId id = GhostProfileId.fromString(profileName);
        this.profile = PersonalityProfile.forId(id);
        this.settings.setGhostProfile(id.getValue());
    }

    public GhostMood getMood() {
        return mood;
    }

    public void setMood(GhostMood newMood) {
        this.mood = newMood;
    }

    public void onCommandExecuted(boolean success, boolean isHighRisk) {
        if (isHighRisk) {
            this.mood = GhostMood.VIGILANT;
        } else if (!success) {
            this.mood = GhostMood.AGITATED;
        } else {
            // Decay toward neutral or curious
            if (this.mood == GhostMood.AGITATED || this.mood == GhostMood.VIGILANT) {
                this.mood = GhostMood.NEUTRAL;
            } else if (random.nextDouble() < 0.25) {
                this.mood = GhostMood.CURIOUS;
            }
        }
    }

    private void checkTimeBasedMood() {
        int hour = LocalTime.now().getHour();
        if (hour >= 23 || hour < 4) {
            this.mood = GhostMood.HAUNTED; // Late night / witching hour
        }
    }

    public String generateGreeting() {
        String greeting = profile.getGreeting();
        if (mood == GhostMood.HAUNTED) {
            greeting = "░▒▓ [The witching hour deepens] " + greeting;
        } else if (mood == GhostMood.VIGILANT) {
            greeting = "[Vigilance Active] " + greeting;
        }
        return greeting;
    }

    public String decorateResponse(String text) {
        if (text == null || text.isBlank()) return text;
        if (settings.isReducedMotion()) return text;

        StringBuilder sb = new StringBuilder();

        // Atmospheric prefix if mood is HAUNTED or AGITATED
        if (mood == GhostMood.HAUNTED && random.nextDouble() < 0.35) {
            String glyph = GLITCH_GLYPHS[random.nextInt(GLITCH_GLYPHS.length)];
            sb.append(glyph).append(" [Echoes in terminal cache] ");
        } else if (mood == GhostMood.VIGILANT && random.nextDouble() < 0.35) {
            sb.append("✦ [Perimeter Verified] ");
        }

        sb.append(text);

        // Subtle ghostly trailer
        if (mood == GhostMood.HAUNTED && random.nextDouble() < 0.25) {
            sb.append("\n  ..the terminal cursor pulses faint green..");
        }

        return sb.toString();
    }
}
