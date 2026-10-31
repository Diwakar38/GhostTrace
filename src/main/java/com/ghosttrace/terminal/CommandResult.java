package com.ghosttrace.terminal;

public record CommandResult(
    String output,
    boolean success,
    boolean shouldExit,
    boolean shouldClear
) {
    public static CommandResult success(String output) {
        return new CommandResult(output, true, false, false);
    }

    public static CommandResult error(String error) {
        return new CommandResult(error, false, false, false);
    }

    public static CommandResult exit() {
        return new CommandResult("Exiting GhostTrace... The digital ghosts return to dormancy.", true, true, false);
    }

    public static CommandResult clear() {
        return new CommandResult("", true, false, true);
    }
}
