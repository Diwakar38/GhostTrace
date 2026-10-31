package com.ghosttrace.guardian;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class GuardianExecutor {

    public record ExecutionResult(
        int exitCode,
        String stdout,
        String stderr,
        long durationMs,
        boolean timedOut
    ) {
        public boolean isSuccess() {
            return exitCode == 0 && !timedOut;
        }
    }

    public ExecutionResult execute(String command, int timeoutSeconds) {
        long start = System.currentTimeMillis();
        boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("win");
        ProcessBuilder pb = isWindows
            ? new ProcessBuilder("cmd.exe", "/c", command)
            : new ProcessBuilder("sh", "-c", command);

        try {
            Process process = pb.start();

            StringBuilder out = new StringBuilder();
            StringBuilder err = new StringBuilder();

            CompletableFuture<Void> outReader = CompletableFuture.runAsync(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        out.append(line).append("\n");
                    }
                } catch (Exception ignored) {}
            });

            CompletableFuture<Void> errReader = CompletableFuture.runAsync(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        err.append(line).append("\n");
                    }
                } catch (Exception ignored) {}
            });

            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return new ExecutionResult(-1, out.toString().trim(), "Execution timed out after " + timeoutSeconds + "s", System.currentTimeMillis() - start, true);
            }

            outReader.get(2, TimeUnit.SECONDS);
            errReader.get(2, TimeUnit.SECONDS);

            long duration = System.currentTimeMillis() - start;
            return new ExecutionResult(process.exitValue(), out.toString().trim(), err.toString().trim(), duration, false);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            return new ExecutionResult(-1, "", "Execution error: " + e.getMessage(), duration, false);
        }
    }
}
