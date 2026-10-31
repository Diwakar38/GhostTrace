package com.ghosttrace.terminal;

import com.ghosttrace.ai.InferenceResult;
import com.ghosttrace.audio.AudioEventId;
import com.ghosttrace.developer.DeveloperIntent;
import com.ghosttrace.ghost.GhostMood;
import com.ghosttrace.ghost.GhostProfileId;
import com.ghosttrace.ghost.PersonalityProfile;
import com.ghosttrace.guardian.CommandAnalysis;
import com.ghosttrace.guardian.GuardianExecutor;
import com.ghosttrace.haunt.EventCategory;
import com.ghosttrace.haunt.HauntMode;
import com.ghosttrace.memory.MemoryItem;
import com.ghosttrace.memory.ProjectInfo;
import com.ghosttrace.system.HealthReport;
import com.ghosttrace.system.SystemTelemetry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandRegistry {

    public record CommandDef(
        String name,
        List<String> aliases,
        String usage,
        String description,
        CommandHandler handler
    ) {}

    private final Map<String, CommandDef> commands = new HashMap<>();
    private final List<CommandDef> orderedCommands = new ArrayList<>();

    public CommandRegistry() {
        registerDefaults();
    }

    public void register(String name, List<String> aliases, String usage, String description, CommandHandler handler) {
        CommandDef def = new CommandDef(name, aliases, usage, description, handler);
        commands.put(name.toLowerCase(), def);
        for (String alias : aliases) {
            commands.put(alias.toLowerCase(), def);
        }
        orderedCommands.add(def);
    }

    public CommandDef find(String name) {
        if (name == null) return null;
        return commands.get(name.trim().toLowerCase());
    }

    public List<CommandDef> getOrderedCommands() {
        return Collections.unmodifiableList(orderedCommands);
    }

    public CommandResult dispatch(CommandContext context, String inputLine) {
        if (inputLine == null || inputLine.isBlank()) {
            return CommandResult.success("");
        }

        List<String> tokens = parseTokens(inputLine);
        if (tokens.isEmpty()) return CommandResult.success("");

        String cmdName = tokens.get(0);
        List<String> args = tokens.subList(1, tokens.size());

        CommandDef def = find(cmdName);
        if (def != null) {
            try {
                return def.handler().execute(context, args);
            } catch (Exception e) {
                return CommandResult.error("Error executing command '" + cmdName + "': " + e.getMessage());
            }
        }

        // If not a registered command, treat as direct conversation query to the ghost AI!
        context.horrorDirector().onKeystroke();
        String contextStr = context.memoryEngine().buildContextForAI();
        InferenceResult result = context.aiEngine().ask(inputLine, contextStr);
        return CommandResult.success(result.content());
    }

    private void registerDefaults() {
        // 1. HELP
        register("help", List.of("?"), "help [command]", "Display available commands and usage instructions.", (ctx, args) -> {
            if (!args.isEmpty()) {
                CommandDef def = find(args.get(0));
                if (def != null) {
                    return CommandResult.success(String.format("Command: %s\nUsage:   %s\nDetails: %s\nAliases: %s",
                        def.name(), def.usage(), def.description(), String.join(", ", def.aliases())));
                }
                return CommandResult.error("Unknown command: " + args.get(0));
            }
            StringBuilder sb = new StringBuilder("=== GHOSTTRACE COMMAND REGISTRY ===\n");
            for (CommandDef d : orderedCommands) {
                sb.append(String.format("  %-10s — %s\n", d.name(), d.description()));
            }
            sb.append("\nTip: Type any plain sentence to converse directly with the local Ghost AI.");
            return CommandResult.success(sb.toString().trim());
        });

        // 2. ABOUT
        register("about", List.of("info"), "about", "Display project purpose, spirit, and tech details.", (ctx, args) -> {
            return CommandResult.success("""
                === ABOUT GHOSTTRACE ===
                GhostTrace is an offline-first, open-weight AI companion and terminal guardian.
                Inspired by the Hacktoberfest 'Ghost in the Machine' challenge, it brings a haunting,
                atmospheric character to your terminal with:
                  • Local AI reasoning (SpectralBrain offline fallback + local Ollama / llama.cpp)
                  • Command Guardian (Automated safety heuristics & HMAC-SHA256 approvals)
                  • Persistent SQLite Memory & Project Indexing
                  • 20 Procedural Horror Audio Sound Effects (javax.sound.sampled DSP)
                  • Retro CRT phosphor aesthetic with dynamic mood transitions
                """);
        });

        // 3. STATUS
        register("status", List.of(), "status", "Display complete application and subsystem status.", (ctx, args) -> {
            SystemTelemetry telem = ctx.systemEngine().getTelemetry();
            StringBuilder sb = new StringBuilder();
            sb.append("=== GHOSTTRACE STATUS REPORT ===\n");
            sb.append(String.format("  Application        : %s v%s (%s)\n", ctx.settings().getAppName(), ctx.settings().getAppVersion(), ctx.settings().getEnvironment()));
            sb.append(String.format("  Ghost Persona      : %s [Mood: %s]\n", ctx.ghostEngine().getProfile().name(), ctx.ghostEngine().getMood().getValue()));
            sb.append(String.format("  Haunt Mode         : %s\n", ctx.hauntEngine().getMode().getValue().toUpperCase()));
            sb.append(String.format("  Guardian Policy    : %s (Block high risk: %s)\n", ctx.settings().isGuardianEnabled() ? "ACTIVE" : "DISABLED", ctx.settings().isGuardianBlockHighRisk()));
            sb.append(String.format("  Persistent Memory  : %d items in SQLite\n", ctx.memoryEngine().getMemoryCount()));
            sb.append(String.format("  Audio Subsystem    : %s (Muted: %s, Master Vol: %d%%)\n", ctx.settings().isAudioEnabled() ? "ENABLED" : "DISABLED", ctx.settings().isAudioMuted(), (int)(ctx.settings().getAudioMasterVolume()*100)));
            sb.append(String.format("  Local Host Metrics : CPU %.1f%% | RAM %d MB used / %d MB max | Uptime %ds\n", telem.cpuUsagePercent(), telem.jvmMemoryUsedMb(), telem.jvmMemoryMaxMb(), telem.uptimeSeconds()));
            return CommandResult.success(sb.toString().trim());
        });

        // 4. VERSION
        register("version", List.of("v"), "version", "Display GhostTrace version.", (ctx, args) -> {
            return CommandResult.success(ctx.settings().getAppName() + " v" + ctx.settings().getAppVersion());
        });

        // 5. CLEAR
        register("clear", List.of("cls"), "clear", "Clear the terminal screen.", (ctx, args) -> {
            return CommandResult.clear();
        });

        // 6. GHOST
        register("ghost", List.of(), "ghost [status|profiles|set <profile>|mood <name>]", "Configure ghost persona and mood.", (ctx, args) -> {
            if (args.isEmpty() || "status".equalsIgnoreCase(args.get(0))) {
                PersonalityProfile p = ctx.ghostEngine().getProfile();
                return CommandResult.success(String.format("Active Profile : %s (%s)\nTagline        : %s\nCurrent Mood   : %s (%s)\nSpeaking Style : %s",
                    p.name(), p.id().getValue(), p.tagline(), ctx.ghostEngine().getMood().name(), ctx.ghostEngine().getMood().getDescription(), p.speakingStyle()));
            }
            String sub = args.get(0).toLowerCase();
            if ("profiles".equals(sub)) {
                return CommandResult.success("""
                    Available Ghost Profiles:
                      1. forgotten-machine : Ancient digital intelligence; reflective, observant, nostalgic.
                      2. phantom-coder     : Playful digital spirit; witty, passionate bug hunter.
                      3. guardian-spirit   : Protective and vigilant; defensive programming and stability.
                    """);
            }
            if ("set".equals(sub)) {
                if (args.size() < 2) return CommandResult.error("Usage: ghost set <forgotten-machine|phantom-coder|guardian-spirit>");
                ctx.ghostEngine().setProfile(args.get(1));
                ctx.settings().save();
                return CommandResult.success("Ghost persona switched to: " + ctx.ghostEngine().getProfile().name());
            }
            if ("mood".equals(sub)) {
                if (args.size() < 2) return CommandResult.error("Usage: ghost mood <NEUTRAL|CURIOUS|PLAYFUL|HAUNTED|AGITATED|VIGILANT>");
                try {
                    GhostMood m = GhostMood.valueOf(args.get(1).toUpperCase());
                    ctx.ghostEngine().setMood(m);
                    return CommandResult.success("Ghost mood updated to: " + m.name());
                } catch (IllegalArgumentException e) {
                    return CommandResult.error("Invalid mood name. Valid moods: NEUTRAL, CURIOUS, PLAYFUL, HAUNTED, AGITATED, VIGILANT");
                }
            }
            return CommandResult.error("Unknown ghost subcommand. Use: ghost [status|profiles|set|mood]");
        });

        // 7. DEV
        register("dev", List.of(), "dev [explain|debug|review|generate|refactor|tests|analyze] [code]", "Developer intelligence & static heuristics.", (ctx, args) -> {
            if (args.isEmpty()) {
                return CommandResult.success("""
                    Developer Subsystem Usage:
                      dev analyze <code>  — Run static heuristics for risk, leaks, and complexity
                      dev review <code>   — Spectral code review with defensive recommendations
                      dev debug <code>    — Debug triage for exceptions and crash traces
                      dev tests <code>    — Generate unit test templates
                      dev explain <code>  — Explain architecture and logic
                    """);
            }
            DeveloperIntent intent = DeveloperIntent.fromString(args.get(0));
            String code = args.size() > 1 ? String.join(" ", args.subList(1, args.size())) : "";
            if (code.isBlank()) {
                return CommandResult.error("Please supply code snippet or error trace after 'dev " + args.get(0) + "'.");
            }
            return CommandResult.success(ctx.developerEngine().handleIntent(intent, code));
        });

        // 8. AI
        register("ai", List.of(), "ai [ask <query>|status|clear]", "Local AI engine management and chat.", (ctx, args) -> {
            if (args.isEmpty() || "status".equalsIgnoreCase(args.get(0))) {
                return CommandResult.success(String.format("AI Endpoint : %s\nTarget Model: %s\nMax Tokens  : %d\nTurn History: %d turns",
                    ctx.settings().getAiEndpoint(), ctx.settings().getAiModel(), ctx.settings().getAiMaxTokens(), ctx.aiEngine().getHistoryTurnCount()));
            }
            if ("clear".equalsIgnoreCase(args.get(0))) {
                ctx.aiEngine().clearHistory();
                return CommandResult.success("Conversation history cleared from AI context buffer.");
            }
            if ("ask".equalsIgnoreCase(args.get(0))) {
                if (args.size() < 2) return CommandResult.error("Usage: ai ask <your question>");
                String query = String.join(" ", args.subList(1, args.size()));
                String projCtx = ctx.memoryEngine().buildContextForAI();
                InferenceResult res = ctx.aiEngine().ask(query, projCtx);
                return CommandResult.success(String.format("[%s :: %dms]\n%s", res.modelName(), res.durationMs(), res.content()));
            }
            return CommandResult.error("Unknown ai subcommand. Use: ai [ask|status|clear]");
        });

        // 9. GUARD
        register("guard", List.of("safety"), "guard [status|analyze <cmd>|explain <cmd>|approve <token>|run <cmd>|history]", "Command Guardian execution safety.", (ctx, args) -> {
            if (args.isEmpty() || "status".equalsIgnoreCase(args.get(0))) {
                return CommandResult.success(String.format("Guardian Status    : %s\nBlock High Risk    : %s\nExecution Timeout  : %ds\nAudit Records      : %d",
                    ctx.settings().isGuardianEnabled() ? "ACTIVE" : "DISABLED", ctx.settings().isGuardianBlockHighRisk(), ctx.settings().getGuardianTimeoutSeconds(), ctx.guardianEngine().getHistory().size()));
            }
            String sub = args.get(0).toLowerCase();
            if ("analyze".equals(sub)) {
                if (args.size() < 2) return CommandResult.error("Usage: guard analyze <command>");
                String target = String.join(" ", args.subList(1, args.size()));
                CommandAnalysis an = ctx.guardianEngine().analyze(target);
                if (an.requiresApproval()) {
                    String token = ctx.guardianEngine().requestApprovalToken(target);
                    return CommandResult.success(String.format("Command: %s\nRisk   : %s\nStatus : %s\nDetails: %s\n\nRequires Approval Token: %s\nApprove with: guard approve %s",
                        an.command(), an.riskLevel().getLabel(), an.blocked() ? "BLOCKED" : "REQUIRES APPROVAL", an.explanation(), token, token));
                }
                return CommandResult.success(String.format("Command: %s\nRisk   : %s\nStatus : PERMITTED\nDetails: %s",
                    an.command(), an.riskLevel().getLabel(), an.explanation()));
            }
            if ("approve".equals(sub)) {
                if (args.size() < 2) return CommandResult.error("Usage: guard approve <token>");
                return CommandResult.success("Approval token recorded. Pass token when executing with 'guard run <token> <command>'.");
            }
            if ("run".equals(sub)) {
                if (args.size() < 2) return CommandResult.error("Usage: guard run [token] <command>");
                String token = null;
                String cmd;
                if (args.size() >= 3 && args.get(1).length() == 16) {
                    token = args.get(1);
                    cmd = String.join(" ", args.subList(2, args.size()));
                } else {
                    cmd = String.join(" ", args.subList(1, args.size()));
                }
                GuardianExecutor.ExecutionResult res = ctx.guardianEngine().executeSafe(cmd, token);
                if (res.isSuccess()) {
                    ctx.horrorDirector().onGuardianApproved();
                    return CommandResult.success(String.format("[Exit Code 0 :: %dms]\n%s", res.durationMs(), res.stdout()));
                } else {
                    ctx.horrorDirector().onGuardianBlocked();
                    return CommandResult.error(String.format("[Exit Code %d :: %dms]\n%s\n%s", res.exitCode(), res.durationMs(), res.stdout(), res.stderr()));
                }
            }
            if ("history".equals(sub)) {
                List<CommandAnalysis> hist = ctx.guardianEngine().getHistory();
                if (hist.isEmpty()) return CommandResult.success("No commands logged in Guardian audit history.");
                StringBuilder sb = new StringBuilder("=== GUARDIAN AUDIT HISTORY ===\n");
                for (CommandAnalysis a : hist) {
                    sb.append(String.format("  [%-8s] %s\n", a.riskLevel().getLabel(), a.command()));
                }
                return CommandResult.success(sb.toString().trim());
            }
            return CommandResult.error("Unknown guard subcommand. Use: guard [status|analyze|explain|run|history]");
        });

        // 10. MEMORY
        register("memory", List.of("mem"), "memory [list|search <q>|save <k> <v>|delete <k>|clear]", "Persistent SQLite developer memory.", (ctx, args) -> {
            if (args.isEmpty() || "list".equalsIgnoreCase(args.get(0))) {
                List<MemoryItem> items = ctx.memoryEngine().listMemories(20);
                if (items.isEmpty()) return CommandResult.success("No memories stored in database. Add with: memory save <key> <content>");
                StringBuilder sb = new StringBuilder("=== STORED DEVELOPER MEMORIES ===\n");
                for (MemoryItem m : items) {
                    sb.append(String.format("  • [%s] (%s): %s\n", m.key(), m.category(), m.content()));
                }
                return CommandResult.success(sb.toString().trim());
            }
            String sub = args.get(0).toLowerCase();
            if ("search".equals(sub)) {
                if (args.size() < 2) return CommandResult.error("Usage: memory search <query>");
                List<MemoryItem> found = ctx.memoryEngine().searchMemories(args.get(1));
                if (found.isEmpty()) return CommandResult.success("No memories matching: " + args.get(1));
                StringBuilder sb = new StringBuilder("=== SEARCH RESULTS ===\n");
                for (MemoryItem m : found) {
                    sb.append(String.format("  • [%s]: %s\n", m.key(), m.content()));
                }
                return CommandResult.success(sb.toString().trim());
            }
            if ("save".equals(sub)) {
                if (args.size() < 3) return CommandResult.error("Usage: memory save <key> <content...>");
                String key = args.get(1);
                String content = String.join(" ", args.subList(2, args.size()));
                boolean ok = ctx.memoryEngine().saveMemory(key, content, "NOTE", "", 1);
                return ok ? CommandResult.success("Memory saved successfully under key: " + key) : CommandResult.error("Failed to save memory.");
            }
            if ("delete".equals(sub)) {
                if (args.size() < 2) return CommandResult.error("Usage: memory delete <key>");
                boolean ok = ctx.memoryEngine().deleteMemory(args.get(1));
                return ok ? CommandResult.success("Deleted memory: " + args.get(1)) : CommandResult.error("Key not found.");
            }
            if ("clear".equals(sub)) {
                ctx.memoryEngine().clearMemories();
                return CommandResult.success("All persistent memories purged.");
            }
            return CommandResult.error("Unknown memory subcommand. Use: memory [list|search|save|delete|clear]");
        });

        // 11. PROJECT
        register("project", List.of("proj"), "project [scan <path> [name]|info]", "Project codebase context indexing.", (ctx, args) -> {
            if (args.isEmpty() || "info".equalsIgnoreCase(args.get(0))) {
                ProjectInfo info = ctx.memoryEngine().getActiveProject();
                if (info == null) return CommandResult.success("No project currently indexed. Index one with: project scan <path> [name]");
                return CommandResult.success(String.format("Project Name   : %s\nPath           : %s\nLanguages      : %s\nIndexed Files  : %d\nTotal Lines    : %d lines",
                    info.name(), info.path(), info.languages(), info.totalFiles(), info.totalLines()));
            }
            if ("scan".equalsIgnoreCase(args.get(0))) {
                if (args.size() < 2) return CommandResult.error("Usage: project scan <path> [name]");
                String path = args.get(1);
                String name = args.size() > 2 ? args.get(2) : "ActiveProject";
                try {
                    ProjectInfo info = ctx.memoryEngine().scanAndRegisterProject(name, path);
                    return CommandResult.success(String.format("Project '%s' scanned successfully!\nFiles: %d | Lines: %d | Languages: %s",
                        info.name(), info.totalFiles(), info.totalLines(), info.languages()));
                } catch (Exception e) {
                    return CommandResult.error("Scan error: " + e.getMessage());
                }
            }
            return CommandResult.error("Unknown project subcommand. Use: project [scan|info]");
        });

        // 12. SYSTEM
        register("system", List.of("sys"), "system [overview|cpu|memory|disk|health]", "Host telemetry and internal diagnostics.", (ctx, args) -> {
            SystemTelemetry t = ctx.systemEngine().getTelemetry();
            if (args.isEmpty() || "overview".equalsIgnoreCase(args.get(0))) {
                return CommandResult.success(String.format("""
                    === HOST TELEMETRY OVERVIEW ===
                      OS / Architecture  : %s (%s)
                      Java Virtual Machine: %s
                      CPU Load Percentage: %.1f%%
                      JVM Memory (Heap)  : %d MB used / %d MB max
                      System RAM         : %d MB free / %d MB total
                      Disk Space (Active): %d GB free / %d GB total
                      Active JVM Threads : %d
                      Session Uptime     : %d seconds
                    """, t.osName(), t.osArch(), t.javaVersion(), t.cpuUsagePercent(), t.jvmMemoryUsedMb(), t.jvmMemoryMaxMb(), t.systemMemoryFreeMb(), t.systemMemoryTotalMb(), t.diskFreeGb(), t.diskTotalGb(), t.activeThreads(), t.uptimeSeconds()).trim());
            }
            if ("health".equalsIgnoreCase(args.get(0))) {
                HealthReport rep = ctx.systemEngine().getHealthReport(ctx.audioManager().isAvailable(), ctx.memoryEngine().getMemoryCount());
                StringBuilder sb = new StringBuilder("=== SUBSYSTEM HEALTH DIAGNOSTICS ===\n");
                sb.append("Overall System Status: [").append(rep.overallStatus().getLabel()).append("]\n");
                for (Map.Entry<String, HealthReport.SubsystemHealth> entry : rep.subsystems().entrySet()) {
                    sb.append(String.format("  %-12s : [%-4s] %s\n", entry.getKey(), entry.getValue().status().getLabel(), entry.getValue().message()));
                }
                return CommandResult.success(sb.toString().trim());
            }
            return CommandResult.error("Unknown system subcommand. Use: system [overview|health]");
        });

        // 13. HAUNT
        register("haunt", List.of(), "haunt [status|mode [normal|ambient|haunted|focus]|trigger <category>]", "Haunted experience & atmospheric events.", (ctx, args) -> {
            if (args.isEmpty() || "status".equalsIgnoreCase(args.get(0))) {
                return CommandResult.success(String.format("Haunt Mode     : %s\nDescription    : %s\nSuppressed     : %s\nTriggered Today: %d events",
                    ctx.hauntEngine().getMode().getValue().toUpperCase(), ctx.hauntEngine().getMode().getDescription(), ctx.hauntEngine().isAtmosphereSuppressed(), ctx.hauntEngine().getEventHistory().size()));
            }
            String sub = args.get(0).toLowerCase();
            if ("mode".equals(sub)) {
                if (args.size() < 2) return CommandResult.error("Usage: haunt mode <normal|ambient|haunted|focus>");
                HauntMode m = HauntMode.fromString(args.get(1));
                ctx.hauntEngine().setMode(m);
                ctx.settings().save();
                return CommandResult.success("Haunt mode set to: " + m.getValue().toUpperCase() + " (" + m.getDescription() + ")");
            }
            if ("trigger".equals(sub)) {
                if (args.size() < 2) return CommandResult.error("Usage: haunt trigger <idle_whisper|glitch_manifestation|late_night_horror>");
                try {
                    EventCategory cat = EventCategory.valueOf(args.get(1).toUpperCase());
                    String banner = ctx.hauntEngine().triggerEvent(cat);
                    return CommandResult.success(banner != null ? banner : "[Atmospheric effect on cooldown or suppressed]");
                } catch (IllegalArgumentException e) {
                    return CommandResult.error("Unknown category. Use: idle_whisper, glitch_manifestation, late_night_horror");
                }
            }
            return CommandResult.error("Unknown haunt subcommand. Use: haunt [status|mode|trigger]");
        });

        // 14. AUDIO
        register("audio", List.of("sound", "sfx"), "audio [status|enable|disable|volume <0-100>|mute|unmute|test]", "Horror audio controls & diagnostics.", (ctx, args) -> {
            if (args.isEmpty() || "status".equalsIgnoreCase(args.get(0))) {
                return CommandResult.success(String.format("Audio Enabled      : %s\nAudio Muted        : %s\nHardware Available : %s\nMaster Volume      : %d%%\nAmbient Volume     : %d%%",
                    ctx.settings().isAudioEnabled() ? "YES" : "NO", ctx.settings().isAudioMuted() ? "YES (MUTED)" : "NO (AUDIBLE)", ctx.audioManager().isAvailable() ? "YES" : "NO", (int)(ctx.audioManager().getMasterVolume()*100), (int)(ctx.settings().getAudioAmbientVolume()*100)));
            }
            String sub = args.get(0).toLowerCase();
            if ("enable".equals(sub)) {
                ctx.settings().setAudioEnabled(true);
                ctx.settings().save();
                return CommandResult.success("Audio subsystem ENABLED.");
            }
            if ("disable".equals(sub)) {
                ctx.settings().setAudioEnabled(false);
                ctx.settings().save();
                return CommandResult.success("Audio subsystem DISABLED.");
            }
            if ("mute".equals(sub)) {
                ctx.audioManager().setMuted(true);
                return CommandResult.success("Audio MUTED.");
            }
            if ("unmute".equals(sub)) {
                ctx.audioManager().setMuted(false);
                return CommandResult.success("Audio UNMUTED.");
            }
            if ("volume".equals(sub)) {
                if (args.size() < 2) return CommandResult.error("Usage: audio volume <0-100>");
                try {
                    int vol = Integer.parseInt(args.get(1));
                    ctx.audioManager().setMasterVolume(vol / 100.0f);
                    return CommandResult.success("Master volume adjusted to " + vol + "%.");
                } catch (NumberFormatException e) {
                    return CommandResult.error("Invalid volume integer (0-100).");
                }
            }
            if ("test".equals(sub)) {
                if (!ctx.audioManager().isAvailable()) {
                    return CommandResult.error("Audio is currently unavailable, disabled, or muted.");
                }
                ctx.audioManager().playTestSound();
                return CommandResult.success("Audible ping dispatched to playback stream.");
            }
            return CommandResult.error("Unknown audio subcommand. Use: audio [status|enable|disable|volume|mute|unmute|test]");
        });

        // 15. EXIT
        register("exit", List.of("quit", "q"), "exit", "Gracefully terminate GhostTrace.", (ctx, args) -> {
            ctx.horrorDirector().dispatch(AudioEventId.SHUTDOWN, com.ghosttrace.audio.HorrorAudioDirector.AudioPriority.IMPORTANT);
            return CommandResult.exit();
        });
    }

    private List<String> parseTokens(String line) {
        List<String> tokens = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"' || c == '\'') {
                inQuotes = !inQuotes;
            } else if (Character.isWhitespace(c) && !inQuotes) {
                if (!current.isEmpty()) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }
        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }
        return tokens;
    }
}
