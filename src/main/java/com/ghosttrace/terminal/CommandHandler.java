package com.ghosttrace.terminal;

import java.util.List;

@FunctionalInterface
public interface CommandHandler {
    CommandResult execute(CommandContext context, List<String> args);
}
