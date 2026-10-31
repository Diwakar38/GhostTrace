package com.ghosttrace;

import com.ghosttrace.ai.AIEngine;
import com.ghosttrace.audio.AudioManager;
import com.ghosttrace.audio.HorrorAudioDirector;
import com.ghosttrace.config.Settings;
import com.ghosttrace.developer.DeveloperEngine;
import com.ghosttrace.ghost.GhostEngine;
import com.ghosttrace.guardian.GuardianEngine;
import com.ghosttrace.haunt.HauntEngine;
import com.ghosttrace.memory.MemoryEngine;
import com.ghosttrace.system.SystemEngine;
import com.ghosttrace.terminal.CommandContext;
import com.ghosttrace.terminal.CommandRegistry;
import com.ghosttrace.terminal.CommandResult;
import com.ghosttrace.terminal.GhostTraceTerminal;
import com.ghosttrace.terminal.TerminalConsole;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Settings settings = new Settings();
        GhostEngine ghostEngine = new GhostEngine(settings);
        GuardianEngine guardianEngine = new GuardianEngine(settings);
        MemoryEngine memoryEngine = new MemoryEngine(settings);
        AIEngine aiEngine = new AIEngine(settings, ghostEngine);
        DeveloperEngine developerEngine = new DeveloperEngine();
        SystemEngine systemEngine = new SystemEngine(settings);
        HauntEngine hauntEngine = new HauntEngine(settings, ghostEngine);
        AudioManager audioManager = new AudioManager(settings);
        HorrorAudioDirector horrorDirector = new HorrorAudioDirector(settings, audioManager);

        CommandContext context = new CommandContext(
            settings,
            ghostEngine,
            guardianEngine,
            memoryEngine,
            aiEngine,
            developerEngine,
            systemEngine,
            hauntEngine,
            audioManager,
            horrorDirector
        );

        if (args == null || args.length == 0) {
            // Launch interactive Terminal REPL
            GhostTraceTerminal terminal = new GhostTraceTerminal(context);
            terminal.run();
        } else {
            // Non-interactive CLI command dispatch
            String fullCommand = String.join(" ", args).trim();
            if (fullCommand.equals("--version") || fullCommand.equals("-v")) {
                fullCommand = "version";
            } else if (fullCommand.equals("--help") || fullCommand.equals("-h")) {
                fullCommand = "help";
            }

            CommandRegistry registry = new CommandRegistry();
            CommandResult result = registry.dispatch(context, fullCommand);

            if (result.output() != null && !result.output().isBlank()) {
                String color = result.success() ? TerminalConsole.GREEN : TerminalConsole.RED;
                System.out.println(color + result.output() + TerminalConsole.RESET);
            }

            audioManager.shutdown();
            System.exit(result.success() ? 0 : 1);
        }
    }
}
