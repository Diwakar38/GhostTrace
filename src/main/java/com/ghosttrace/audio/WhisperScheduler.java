package com.ghosttrace.audio;

import com.ghosttrace.config.Settings;
import com.ghosttrace.haunt.HauntMode;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WhisperScheduler {

    private final Settings settings;
    private final HorrorAudioDirector director;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ghosttrace-whisper-scheduler");
        t.setDaemon(true);
        return t;
    });

    private final Random random = new Random();
    private volatile long lastActivityTime = System.currentTimeMillis();

    public WhisperScheduler(Settings settings, HorrorAudioDirector director) {
        this.settings = settings;
        this.director = director;
    }

    public void start() {
        // Check periodically every 25 seconds
        scheduler.scheduleWithFixedDelay(this::tick, 30, 25, TimeUnit.SECONDS);
    }

    public void recordActivity() {
        this.lastActivityTime = System.currentTimeMillis();
    }

    private void tick() {
        try {
            HauntMode mode = HauntMode.fromString(settings.getHauntMode());
            if (mode == HauntMode.FOCUS || mode == HauntMode.NORMAL) return;

            long idleMs = System.currentTimeMillis() - lastActivityTime;
            // If idle for over 40 seconds
            if (idleMs > 40_000) {
                double chance = (mode == HauntMode.HAUNTED) ? 0.60 : 0.25;
                if (random.nextDouble() < chance) {
                    director.onWhisper();
                }
            }
        } catch (Throwable ignored) {}
    }

    public void stop() {
        scheduler.shutdownNow();
    }
}
