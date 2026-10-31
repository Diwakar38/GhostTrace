package com.ghosttrace.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Central configuration settings for GhostTrace.
 * Supports environment variable overrides and file-based persistence.
 */
public class Settings {

    public static final String APP_NAME = "GhostTrace";
    public static final String APP_VERSION = "1.2.0";

    private String environment = "production";
    private String ghostProfile = "forgotten-machine";
    private String hauntMode = "haunted";
    private boolean audioEnabled = true;
    private float audioMasterVolume = 0.80f;
    private float audioAmbientVolume = 0.30f;
    private boolean audioMuted = false;
    private boolean guardianEnabled = true;
    private boolean guardianBlockHighRisk = true;
    private int guardianTimeoutSeconds = 30;
    private String aiEndpoint = "http://localhost:11434";
    private String aiModel = "qwen2.5:1.5b";
    private int aiMaxTokens = 512;
    private float aiTemperature = 0.70f;
    private boolean reducedMotion = false;

    private final Path dataDir;
    private final Path configPath;
    private final Path dbPath;

    public Settings() {
        this(resolveDefaultDataDir());
    }

    private static Path resolveDefaultDataDir() {
        String envDataDir = System.getenv("GHOSTTRACE_DATA_DIR");
        if (envDataDir != null && !envDataDir.isBlank()) {
            return Paths.get(envDataDir);
        }
        try {
            Path home = Paths.get(System.getProperty("user.home", "."));
            Path candidate = home.resolve(".ghosttrace");
            if (Files.exists(candidate) && Files.isWritable(candidate)) {
                return candidate;
            }
            if (Files.isWritable(home)) {
                return candidate;
            }
        } catch (Exception ignored) {}
        return Paths.get(".ghosttrace");
    }

    public Settings(Path dataDir) {
        this.dataDir = dataDir;
        this.configPath = this.dataDir.resolve("config.properties");
        this.dbPath = this.dataDir.resolve("memory.db");
        ensureDirectories();
        load();
    }

    private void ensureDirectories() {
        try {
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
        } catch (IOException e) {
            System.err.println("Warning: could not create GhostTrace data dir: " + e.getMessage());
        }
    }

    public synchronized void load() {
        // 1. Read from properties file if it exists
        if (Files.exists(configPath)) {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(configPath.toFile())) {
                props.load(in);
                this.ghostProfile = props.getProperty("ghostProfile", this.ghostProfile);
                this.hauntMode = props.getProperty("hauntMode", this.hauntMode);
                this.audioEnabled = Boolean.parseBoolean(props.getProperty("audioEnabled", String.valueOf(this.audioEnabled)));
                this.audioMasterVolume = Float.parseFloat(props.getProperty("audioMasterVolume", String.valueOf(this.audioMasterVolume)));
                this.audioAmbientVolume = Float.parseFloat(props.getProperty("audioAmbientVolume", String.valueOf(this.audioAmbientVolume)));
                this.audioMuted = Boolean.parseBoolean(props.getProperty("audioMuted", String.valueOf(this.audioMuted)));
                this.guardianEnabled = Boolean.parseBoolean(props.getProperty("guardianEnabled", String.valueOf(this.guardianEnabled)));
                this.guardianBlockHighRisk = Boolean.parseBoolean(props.getProperty("guardianBlockHighRisk", String.valueOf(this.guardianBlockHighRisk)));
                this.guardianTimeoutSeconds = Integer.parseInt(props.getProperty("guardianTimeoutSeconds", String.valueOf(this.guardianTimeoutSeconds)));
                this.aiEndpoint = props.getProperty("aiEndpoint", this.aiEndpoint);
                this.aiModel = props.getProperty("aiModel", this.aiModel);
                this.aiMaxTokens = Integer.parseInt(props.getProperty("aiMaxTokens", String.valueOf(this.aiMaxTokens)));
                this.aiTemperature = Float.parseFloat(props.getProperty("aiTemperature", String.valueOf(this.aiTemperature)));
                this.reducedMotion = Boolean.parseBoolean(props.getProperty("reducedMotion", String.valueOf(this.reducedMotion)));
            } catch (Exception e) {
                // Keep default values
            }
        }

        // 2. Environment variable overrides (GHOSTTRACE_*)
        String envProfile = System.getenv("GHOSTTRACE_PROFILE");
        if (envProfile != null && !envProfile.isBlank()) this.ghostProfile = envProfile;

        String envMode = System.getenv("GHOSTTRACE_HAUNT_MODE");
        if (envMode != null && !envMode.isBlank()) this.hauntMode = envMode;

        String envAudio = System.getenv("GHOSTTRACE_AUDIO_ENABLED");
        if (envAudio != null) this.audioEnabled = Boolean.parseBoolean(envAudio);

        String envAiUrl = System.getenv("GHOSTTRACE_AI_ENDPOINT");
        if (envAiUrl != null && !envAiUrl.isBlank()) this.aiEndpoint = envAiUrl;

        String envAiModel = System.getenv("GHOSTTRACE_AI_MODEL");
        if (envAiModel != null && !envAiModel.isBlank()) this.aiModel = envAiModel;
    }

    public synchronized void save() {
        Properties props = new Properties();
        props.setProperty("ghostProfile", this.ghostProfile);
        props.setProperty("hauntMode", this.hauntMode);
        props.setProperty("audioEnabled", String.valueOf(this.audioEnabled));
        props.setProperty("audioMasterVolume", String.valueOf(this.audioMasterVolume));
        props.setProperty("audioAmbientVolume", String.valueOf(this.audioAmbientVolume));
        props.setProperty("audioMuted", String.valueOf(this.audioMuted));
        props.setProperty("guardianEnabled", String.valueOf(this.guardianEnabled));
        props.setProperty("guardianBlockHighRisk", String.valueOf(this.guardianBlockHighRisk));
        props.setProperty("guardianTimeoutSeconds", String.valueOf(this.guardianTimeoutSeconds));
        props.setProperty("aiEndpoint", this.aiEndpoint);
        props.setProperty("aiModel", this.aiModel);
        props.setProperty("aiMaxTokens", String.valueOf(this.aiMaxTokens));
        props.setProperty("aiTemperature", String.valueOf(this.aiTemperature));
        props.setProperty("reducedMotion", String.valueOf(this.reducedMotion));

        try (FileOutputStream out = new FileOutputStream(configPath.toFile())) {
            props.store(out, "GhostTrace Configuration");
        } catch (IOException e) {
            System.err.println("Warning: could not save config.properties: " + e.getMessage());
        }
    }

    // Getters and Setters
    public String getAppName() { return APP_NAME; }
    public String getAppVersion() { return APP_VERSION; }
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public String getGhostProfile() { return ghostProfile; }
    public void setGhostProfile(String ghostProfile) { this.ghostProfile = ghostProfile; }

    public String getHauntMode() { return hauntMode; }
    public void setHauntMode(String hauntMode) { this.hauntMode = hauntMode; }

    public boolean isAudioEnabled() { return audioEnabled; }
    public void setAudioEnabled(boolean audioEnabled) { this.audioEnabled = audioEnabled; }

    public float getAudioMasterVolume() { return audioMasterVolume; }
    public void setAudioMasterVolume(float audioMasterVolume) { this.audioMasterVolume = Math.clamp(audioMasterVolume, 0.0f, 1.0f); }

    public float getAudioAmbientVolume() { return audioAmbientVolume; }
    public void setAudioAmbientVolume(float audioAmbientVolume) { this.audioAmbientVolume = Math.clamp(audioAmbientVolume, 0.0f, 1.0f); }

    public boolean isAudioMuted() { return audioMuted; }
    public void setAudioMuted(boolean audioMuted) { this.audioMuted = audioMuted; }

    public boolean isGuardianEnabled() { return guardianEnabled; }
    public void setGuardianEnabled(boolean guardianEnabled) { this.guardianEnabled = guardianEnabled; }

    public boolean isGuardianBlockHighRisk() { return guardianBlockHighRisk; }
    public void setGuardianBlockHighRisk(boolean guardianBlockHighRisk) { this.guardianBlockHighRisk = guardianBlockHighRisk; }

    public int getGuardianTimeoutSeconds() { return guardianTimeoutSeconds; }
    public void setGuardianTimeoutSeconds(int guardianTimeoutSeconds) { this.guardianTimeoutSeconds = guardianTimeoutSeconds; }

    public String getAiEndpoint() { return aiEndpoint; }
    public void setAiEndpoint(String aiEndpoint) { this.aiEndpoint = aiEndpoint; }

    public String getAiModel() { return aiModel; }
    public void setAiModel(String aiModel) { this.aiModel = aiModel; }

    public int getAiMaxTokens() { return aiMaxTokens; }
    public void setAiMaxTokens(int aiMaxTokens) { this.aiMaxTokens = aiMaxTokens; }

    public float getAiTemperature() { return aiTemperature; }
    public void setAiTemperature(float aiTemperature) { this.aiTemperature = aiTemperature; }

    public boolean isReducedMotion() { return reducedMotion; }
    public void setReducedMotion(boolean reducedMotion) { this.reducedMotion = reducedMotion; }

    public Path getDataDir() { return dataDir; }
    public Path getConfigPath() { return configPath; }
    public Path getDbPath() { return dbPath; }
}
