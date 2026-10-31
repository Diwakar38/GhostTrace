---
title: GhostTrace — A Local AI That Makes Your Terminal Feel Haunted (Java Edition)
published: true
tags: java, devchallenge, hf26challenge, ai, opensource
canonical_url: https://dev.to/diwakar38/ghosttrace-a-local-ai-that-makes-your-terminal-feel-haunted-java-edition
cover_image: https://raw.githubusercontent.com/Diwakar38/GhostTrace/main/assets/cover.png
description: An offline-first local AI companion, autonomous safety guardian, and procedural horror experience built in modern Java 21.
---

*This is a submission for the Hacktoberfest Open-Source AI Challenge Week 4: Ghost in the Machine.*

---

## 1. What I Built

**GhostTrace (Java Edition)** is an offline-first, local terminal companion designed for developers, wrapped inside an atmospheric, haunted persona.

When approaching the *Ghost in the Machine* theme, I wanted to explore what happens when your command-line interface stops behaving like a passive pipe and starts feeling like a living, conscious digital entity. Instead of another cloud-tethered chatbot wrapper, GhostTrace turns the terminal into an interactive haunted mainframe that observes your commands, reacts to your coding sessions, and protects your system.

```
╔═════════════════════════════════════════════════════════════════════════════╗
║  ██████╗ ██╗  ██╗ ██████╗ ███████╗████████╗████████╗██████╗  █████╗  ██████╗███████╗ ║
║ ██╔════╝ ██║  ██║██╔═══██╗██╔════╝╚══██╔══╝╚══██╔══╝██╔══██╗██╔══██╗██╔════╝██╔════╝ ║
║ ██║  ███╗███████║██║   ██║███████╗   ██║      ██║   ██████╔╝███████║██║     █████╗   ║
║ ██║   ██║██╔══██║██║   ██║╚════██║   ██║      ██║   ██╔══██╗██╔══██║██║     ██╔══╝   ║
║ ╚██████╔╝██║  ██║╚██████╔╝███████║   ██║      ██║   ██║  ██║██║  ██║╚██████╗███████╗ ║
║  ╚═════╝ ╚═╝  ╚═╝ ╚═════╝ ╚══════╝   ╚═╝      ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝╚══════╝ ║
║                                                                             ║
║  [+] THE AI THAT HAUNTS YOUR TERMINAL  ::  JAVA EDITION v1.2.0             ║
║  [+] Offline-First  ::  Autonomous Safety Guardian  ::  Haunted Memory       ║
╚═════════════════════════════════════════════════════════════════════════════╝
```

### Core Features

- **Local AI & Spectral Brain:** Local chat completion with Qwen2.5-1.5B via Ollama / llama.cpp servers, plus a built-in rule-based reasoning fallback (*Spectral Brain*) that works completely offline with zero downloaded weights.
- **Three Ghost Personalities:**
  - *Forgotten Machine:* Calm, observant, reflective, ancient digital intelligence.
  - *Phantom Coder:* Lively, witty, enthusiastic bug-hunter and algorithm puzzle enthusiast.
  - *Guardian Spirit:* Composed, vigilant, structured, protective of stability and clean architecture.
- **Command Guardian & Security Engine:** Automated heuristic pattern matching for destructive operations (`rm -rf`, raw disk overwrites `dd of=/dev/...`, system shutdowns, `curl ... | bash`, `git reset --hard`) with HMAC-SHA256 single-use cryptographic approval tokens.
- **Persistent SQLite Memory:** Persistent developer facts, notes, and code snippets stored in embedded SQLite using WAL (Write-Ahead Logging) mode with keyword search.
- **Project Context Scanner:** Recursive codebase indexer that analyzes line counts, file distributions, and languages while ignoring build artifacts.
- **Procedural Horror Audio Engine:** 20 original 44.1kHz 16-bit PCM sound effects (ambient wind, dark hums, static bursts, whispers, keystroke clicks, guardian approvals) mixed polyphonically via `javax.sound.sampled`, plus pure-Java procedural DSP synthesis.
- **Interactive Phosphor-Green CRT TUI:** High-performance terminal REPL powered by **JLine 3**, featuring tab auto-completion, persistent command history, ANSI box borders, and glitch manifestations (`░▒▓█`).

---

## 2. Code

- **GitHub Repository:** [https://github.com/Diwakar38/GhostTrace](https://github.com/Diwakar38/GhostTrace)
- **License:** MIT
- **Runtime:** Java 21+ LTS (OpenJDK)

---

## 3. Technology Stack

- **Language:** Java 21+ (utilizing records, pattern matching, modern switch expressions, and virtual-thread-ready executors)
- **Terminal Engine:** JLine 3 (`TerminalBuilder`, `LineReader`, `Completer`, `DefaultHistory`)
- **Persistence:** SQLite via `sqlite-jdbc` (WAL mode enabled)
- **Serialization:** Jackson Databind 2.17
- **Networking:** Standard library `java.net.http.HttpClient`
- **Audio & DSP:** Standard library `javax.sound.sampled` (zero native audio dependencies)
- **Testing:** JUnit 5 (Jupiter Platform) — 36 comprehensive automated unit tests
- **Build System:** Apache Maven (configured with `maven-assembly-plugin` to output a standalone fat JAR)

---

## 4. Architecture & Subsystems

The application is structured into decoupled, event-driven subsystems:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       GhostTrace Java Architecture                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │               Interactive Terminal REPL (JLine 3)                   │   │
│   │       Phosphor CRT Glow  ::  Auto-Completion  ::  History           │   │
│   └───────────────────┬─────────────────────────────┬───────────────────┘   │
│                       │                             │                       │
│        ┌──────────────▼─────────────┐ ┌─────────────▼──────────────┐        │
│        │   Ghost Persona & Mood     │ │      Command Guardian      │        │
│        │  3 Profiles & Mood Decay   │ │  Risk Rules & HMAC Tokens  │        │
│        └──────────────┬─────────────┘ └─────────────┬──────────────┘        │
│                       │                             │                       │
│        ┌──────────────▼─────────────┐ ┌─────────────▼──────────────┐        │
│        │   Local AI / SpectralBrain │ │   Persistent SQLite Memory │        │
│        │ Ollama HTTP + Offline Fall │ │   WAL Mode & Code Indexer  │        │
│        └──────────────┬─────────────┘ └─────────────┬──────────────┘        │
│                       │                             │                       │
│        ┌──────────────▼─────────────┐ ┌─────────────▼──────────────┐        │
│        │  Horror Audio & DSP Mixer  │ │    Host System Telemetry   │        │
│        │ javax.sound 20 WAVs + Synth│ │  CPU, RAM, Disk, Health    │        │
│        └────────────────────────────┘ └────────────────────────────┘        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### A. Command Guardian & Security Approvals
Before executing potentially destructive operations in the terminal, the Guardian intercepts and classifies command risk (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`):

```java
CommandAnalysis analysis = guardianEngine.analyze("rm -rf /");

// Flagged:
// Risk   : Critical Risk
// Status : BLOCKED
// Details: Matched [DEL-001] Recursive Force File Deletion
// Token  : Wi05M9P3zb05xwJ0 (Single-use HMAC-SHA256 signature)
```

High-risk actions require a cryptographic approval token (`guard approve <token>`) before execution is permitted, preventing accidental recursive file wipes or destructive git history loss.

### B. Dual-Layer AI: Local Inference + Spectral Brain
GhostTrace interfaces with a locally hosted LLM (such as `Qwen2.5-1.5B-Instruct` running under Ollama) via async HTTP calls:

```java
// Connects to local endpoint: http://localhost:11434/api/chat
InferenceResult res = aiEngine.ask("How do I avoid deadlocks in Java?", projectContext);
```

If no local model server is currently active, GhostTrace never crashes. It immediately invokes its **Spectral Brain**—an offline heuristics engine that analyzes stack traces, algorithmic complexity, concurrency pitfalls, and memory leaks while preserving the active ghost personality.

### C. Pure Java Procedural DSP & Audio Mixing
GhostTrace includes 20 original horror sound effects packaged directly in the JAR. To guarantee zero dependency on external audio assets, it also includes a custom procedural DSP synthesizer (`AudioSynthesizer.java`):

```java
// Procedural frequency modulation in javax.sound.sampled
float f1 = 220.0f + 110.0f * (t / duration);
float env = (float) Math.sin(Math.PI * (t / duration));
left = (float) (Math.sin(2 * Math.PI * f1 * t) * 0.4f * env);
```

The `HorrorAudioDirector` coordinates sound events by priority (`CRITICAL` > `IMPORTANT` > `ATMOSPHERIC` > `AMBIENT`) and automatically suppresses atmospheric noises when the user switches to `FOCUS` mode.

---

## 5. Quick Demonstration

### Launching the Terminal
```bash
./ghosttrace
```

### Static Command Security Analysis
```bash
$ guard analyze "git reset --hard origin/main"

Command: git reset --hard origin/main
Risk   : High Risk
Status : REQUIRES APPROVAL
Details: Matched 1 safety rule(s):
  • [VCS-002] Hard Git Reset (High Risk): Discards all uncommitted changes.
    Alternative: Stash changes first using 'git stash' before resetting.

Requires Approval Token: fK30d9S1vP02lQ88
```

### Persistent SQLite Memory
```bash
$ memory save db_host "10.0.0.1"
Memory saved successfully under key: db_host

$ memory list
=== STORED DEVELOPER MEMORIES ===
  • [db_host] (NOTE): 10.0.0.1
```

### Automated Code Review
```bash
$ dev analyze "public class Test { void run() { try {} catch(Exception e){} } }"

=== STATIC CODE ANALYSIS REPORT ===
  Detected Language  : Java
  Total Lines        : 1
  Complexity Score   : 2
  Potential Issues   : 1
    • Line 1: Swallowed exception in empty catch block. Logs or recovery are required.
```

---

## 6. Why Open Innovation Matters

Building GhostTrace in Java showcases what open innovation enables:

1. **Complete Data Sovereignty:** Proprietary source code, terminal commands, and system context stay 100% on your machine.
2. **Platform Portability:** Java's virtual machine allows GhostTrace to run seamlessly across Linux, macOS, and Windows with identical terminal aesthetics and sound playback.
3. **No Hidden Cloud Costs:** Developers can prototype and experiment without subscription barriers, rate limits, or API keys.
4. **Resilient Offline Architecture:** With the embedded SQLite database and Spectral Brain fallback, the tool functions during remote flights, train rides, or air-gapped secure development environments.

---

## 7. The Spirit of It

What if your terminal didn't feel like an empty command line? What if opening your shell late at night felt like awakening a dormant intelligence from the circuits?

GhostTrace proves that developer tools can be safe, highly functional, and simultaneously imaginative. It is a little bit of systems engineering, a little bit of cybersecurity, and a lot of atmosphere.
