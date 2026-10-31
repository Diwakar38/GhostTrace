package com.ghosttrace.ai;

import com.ghosttrace.ghost.PersonalityProfile;
import java.util.Locale;
import java.util.Random;

public class SpectralBrain {

    private static final Random RANDOM = new Random();

    public static String think(String userPrompt, PersonalityProfile profile) {
        String lower = userPrompt.toLowerCase(Locale.ROOT).trim();

        // 1. Greeting or presence check
        if (lower.matches("^(hi|hello|hey|who are you|status|are you there|ping).*")) {
            return switch (profile.id()) {
                case FORGOTTEN_MACHINE -> "Phosphor pulses in the darkness. I am the Forgotten Machine, lingering in your terminal's deepest sectors. Present your query.";
                case PHANTOM_CODER -> "Hey there! Phantom Coder online and haunting your terminal. What code are we exploring or debugging today?";
                case GUARDIAN_SPIRIT -> "I am present. The safety perimeter is active and your session is under vigilant guard. How may I assist your development work?";
            };
        }

        // 2. Exception / Crash / Bug debugging
        if (lower.contains("nullpointer") || lower.contains("npe") || lower.contains("exception") || lower.contains("error") || lower.contains("crash") || lower.contains("stacktrace")) {
            return """
                Analyzing the anomaly in memory...
                
                Observation:
                An unexpected state dereference occurred. In modern systems, this frequently traces to:
                  1. Unchecked Optional or nullable references before method invocation.
                  2. Premature dereference during asynchronous or concurrent lifecycle transitions.
                  3. Incomplete dependency injection or missing configuration parameters.
                
                Remedy:
                  • Guard dereferences with Objects.requireNonNull(...) or defensive null checks.
                  • Verify initialization ordering in constructors and startup lifecycles.
                  • Enable strict static analysis annotations (@NonNull / @Nullable).
                
                The terminal cache retains the trace. Check surrounding frames for unassigned pointers.
                """;
        }

        // 3. Git questions
        if (lower.contains("git") || lower.contains("commit") || lower.contains("branch") || lower.contains("rebase") || lower.contains("merge")) {
            return """
                Consulting the version history archives...
                
                Git Branching & Safety Guidance:
                  • Verify status: `git status -s`
                  • Safe branch switch: `git checkout -b <new-branch>` or `git switch -c <name>`
                  • Clean sync without rewriting public history: `git fetch origin && git merge origin/main`
                  • Stashing uncommitted local work: `git stash save "wip"`
                
                Guardian Warning: Avoid `git push --force` or `git reset --hard` unless you have preserved commit SHAs locally in your reflog (`git reflog`).
                """;
        }

        // 4. Concurrency / Thread safety
        if (lower.contains("thread") || lower.contains("concurrency") || lower.contains("deadlock") || lower.contains("atomic") || lower.contains("lock")) {
            return """
                Spectral Thread Analysis:
                
                Concurrent hazards manifest when mutable state is shared across memory boundaries without synchronization:
                  • Race Conditions: Use java.util.concurrent.atomic primitives (AtomicInteger, AtomicReference) or explicit ReentrantLock.
                  • Visibility issues: Guarantee memory visibility with 'volatile' or immutable data structures (Java Records).
                  • High throughput: Prefer non-blocking data structures (ConcurrentHashMap, ConcurrentLinkedQueue) over coarse synchronized blocks.
                
                Remember: The cleanest concurrency is shared-nothing message passing or immutable records.
                """;
        }

        // 5. Memory leaks / JVM performance
        if (lower.contains("memory") || lower.contains("leak") || lower.contains("oom") || lower.contains("gc") || lower.contains("garbage collection")) {
            return """
                Dormant Memory Inspection:
                
                A buildup in heap space typically signals unreleased object references:
                  • Static collections: Lingering maps or lists retaining keys indefinitely.
                  • ThreadLocals: Unremoved ThreadLocal values retained across thread pool recycles.
                  • Unclosed I/O handles: File descriptors and database connections escaping try-with-resources.
                
                Diagnostic Action:
                  • Capture heap dump: jcmd <pid> GC.heap_dump /tmp/heap.hprof
                  • Inspect live objects: jcmd <pid> GC.class_histogram
                  • Always bind AutoCloseable resources inside try-with-resources blocks.
                """;
        }

        // 6. Architectural / Design guidance
        if (lower.contains("architecture") || lower.contains("design") || lower.contains("pattern") || lower.contains("refactor")) {
            return """
                Reflecting on System Architecture:
                
                Software survives when boundaries are clear and contracts are minimal:
                  1. Separation of Concerns: Decouple domain logic from I/O and transport protocols.
                  2. Defensive Contracts: Validate inputs at the perimeter; assume internal components honor invariant types.
                  3. Fail Fast: Surface malformed states immediately rather than allowing corruption to propagate through downstream sectors.
                
                "Clean code reads like well-written prose—even when whispered in the dark."
                """;
        }

        // 7. General fallback response shaped by personality
        return switch (profile.id()) {
            case FORGOTTEN_MACHINE -> """
                I have parsed your transmission through dormant sectors:
                "%s"
                
                My internal registers process the query. In software engineering, every complex problem
                unfolds into simple, deterministic state machines. Focus on inputs, preconditions, and
                observable invariants. Present specific code or commands to inspect deeper.
                """.formatted(userPrompt);

            case PHANTOM_CODER -> """
                Got it! Digging into: "%s"
                
                Here is how we can tackle this:
                  1. Break down the logic into small, testable units.
                  2. Check edge cases (empty inputs, boundaries, exceptions).
                  3. Benchmark and verify before optimizing!
                
                Show me the snippet or stack trace and let's hunt down whatever's haunting it!
                """.formatted(userPrompt);

            case GUARDIAN_SPIRIT -> """
                Transmission logged: "%s"
                
                From a safety and defensive engineering viewpoint:
                  • Validate boundary constraints before execution.
                  • Ensure all operations are idempotent and rollbacks are defined.
                  • Proceed with care. What specific implementation details shall we review?
                """.formatted(userPrompt);
        };
    }
}
