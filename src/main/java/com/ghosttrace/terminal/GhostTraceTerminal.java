package com.ghosttrace.terminal;

import com.ghosttrace.audio.AudioEventId;
import com.ghosttrace.audio.AudioManager;
import com.ghosttrace.audio.HorrorAudioDirector;
import com.ghosttrace.audio.WhisperScheduler;
import com.ghosttrace.config.Settings;
import com.ghosttrace.haunt.EventCategory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class GhostTraceTerminal {

    private final CommandContext context;
    private final CommandRegistry registry = new CommandRegistry();
    private final WhisperScheduler whisperScheduler;

    public GhostTraceTerminal(CommandContext context) {
        this.context = context;
        this.whisperScheduler = new WhisperScheduler(context.settings(), context.horrorDirector());
    }

    public void run() {
        Terminal terminal;
        try {
            terminal = TerminalBuilder.builder()
                .system(true)
                .dumb(false)
                .name("GhostTrace")
                .build();
        } catch (IOException e) {
            System.err.println("Fallback to basic console: " + e.getMessage());
            try {
                terminal = TerminalBuilder.builder().dumb(true).build();
            } catch (IOException ex) {
                throw new RuntimeException("Cannot initialize terminal", ex);
            }
        }

        // Build JLine autocompleter
        List<String> completions = new ArrayList<>();
        for (CommandRegistry.CommandDef def : registry.getOrderedCommands()) {
            completions.add(def.name());
            completions.addAll(def.aliases());
        }
        Completer completer = new StringsCompleter(completions);

        Path historyFile = context.settings().getDataDir().resolve("history");
        LineReader reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(completer)
            .variable(LineReader.HISTORY_FILE, historyFile)
            .history(new DefaultHistory())
            .build();

        // Print header & welcome banner
        TerminalConsole.printHeader();
        context.horrorDirector().dispatch(AudioEventId.STARTUP, HorrorAudioDirector.AudioPriority.IMPORTANT);

        String greeting = context.ghostEngine().generateGreeting();
        TerminalConsole.printBox(
            "COMMUNICATION INITIALIZED",
            TerminalConsole.green(greeting) + "\n" +
            TerminalConsole.gray("Profile: " + context.ghostEngine().getProfile().name() + " | Mode: " + context.hauntEngine().getMode().getValue() + " | Type 'help' for instructions."),
            TerminalConsole.DARK_GREEN,
            TerminalConsole.GREEN
        );

        whisperScheduler.start();

        String prompt = TerminalConsole.GREEN + "ghost@machine" + TerminalConsole.GRAY + ":" + TerminalConsole.CYAN + "~$" + TerminalConsole.RESET + " ";

        boolean running = true;
        while (running) {
            String line;
            try {
                // Check if background idle whisper or glitch manifests
                String idleEvent = context.hauntEngine().checkIdleTrigger();
                if (idleEvent != null) {
                    terminal.writer().println(TerminalConsole.amber(idleEvent));
                    terminal.flush();
                    context.horrorDirector().onGlitch();
                }

                line = reader.readLine(prompt);
                whisperScheduler.recordActivity();

                if (line == null) continue;
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;

                // Dispatch command
                CommandResult result = registry.dispatch(context, trimmed);

                if (result.shouldClear()) {
                    terminal.puts(org.jline.utils.InfoCmp.Capability.clear_screen);
                    terminal.flush();
                    continue;
                }

                if (result.output() != null && !result.output().isBlank()) {
                    String color = result.success() ? TerminalConsole.GREEN : TerminalConsole.RED;
                    terminal.writer().println(color + result.output() + TerminalConsole.RESET);
                    terminal.flush();
                }

                context.ghostEngine().onCommandExecuted(result.success(), false);

                if (result.shouldExit()) {
                    running = false;
                }

            } catch (UserInterruptException e) {
                terminal.writer().println(TerminalConsole.amber("\n[Ctrl+C] Session interrupted. Type 'exit' to terminate safely."));
                terminal.flush();
            } catch (EndOfFileException e) {
                terminal.writer().println(TerminalConsole.gray("\n[Ctrl+D] EOF received. Returning to darkness..."));
                terminal.flush();
                running = false;
            } catch (Exception e) {
                terminal.writer().println(TerminalConsole.red("Error: " + e.getMessage()));
                terminal.flush();
            }
        }

        whisperScheduler.stop();
        context.audioManager().shutdown();
    }
}
