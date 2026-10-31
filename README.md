# GhostTrace — Java Edition

> *"The AI That Haunts Your Terminal."*

GhostTrace (Java Edition) is an offline-first, open-weight AI companion and terminal safety guardian engineered in modern Java 21+. Inspired by the Hacktoberfest *Ghost in the Machine* challenge, it transforms your command-line interface into an atmospheric, phosphor-green living workspace that monitors dangerous commands, persists developer memory, synthesizes procedural horror audio, and assists your coding workflow.

---

## 1. System Architecture

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

---

## 2. Core Subsystems

### 1. Ghost Persona & Mood Engine (`com.ghosttrace.ghost`)
- **Three Unique Personalities:**
  - **Forgotten Machine:** Calm, observant, reflective, ancient digital intelligence.
  - **Phantom Coder:** Lively, witty, enthusiastic bug-hunter and algorithm puzzle lover.
  - **Guardian Spirit:** Vigilant, composed, structured, and protective of system stability.
- **Dynamic Mood State:** Transitions between `NEUTRAL`, `CURIOUS`, `PLAYFUL`, `HAUNTED`, `AGITATED`, and `VIGILANT` based on command outcomes, risks, and time of day (e.g. witching hour after 11 PM).
- **Atmospheric Decoration:** Decorates output with CRT glitch glyphs (`░▒▓█`, `†`, `✦`, `☠`).

### 2. Command Guardian & Execution Safety (`com.ghosttrace.guardian`)
- **Automated Heuristic Analysis:** Detects destructive commands before they execute:
  - Recursive deletions (`rm -rf`, `rmdir /s /q`)
  - Direct disk partition writes (`dd of=/dev/...`, `mkfs`)
  - System shutdowns (`shutdown`, `reboot`, `kill -9 1`)
  - Piped remote execution (`curl ... | bash`)
  - Permissive permissions (`chmod 777`)
  - Dangerous version control rewriting (`git reset --hard`, `git push --force`)
- **Cryptographic Approval Tokens:** Generates time-limited HMAC-SHA256 single-use tokens for elevated commands.
- **Process Isolation:** Runs subprocesses with timeouts and stdout/stderr capture.

### 3. Local AI Engine & Spectral Brain (`com.ghosttrace.ai`)
- **Pluggable Local AI:** Connects to local Ollama (`http://localhost:11434/api/chat`) or llama.cpp servers with zero external cloud dependencies.
- **Built-in Offline Spectral Brain:** If no local model server is currently active, GhostTrace seamlessly falls back to its internal reasoning engine, providing expert programming advice, bug triage, and architectural guidance in the authentic voice of the active ghost.

### 4. Developer Intelligence Subsystem (`com.ghosttrace.developer`)
- Language detection across Java, Python, JavaScript/TypeScript, Go, Rust, C/C++, Shell, and SQL.
- Static heuristics checking for unclosed resources, swallowed exceptions (empty catch blocks), hardcoded credentials, and cyclomatic complexity.
- Test stub generation and refactoring advice.

### 5. Persistent SQLite Memory & Project Indexer (`com.ghosttrace.memory`)
- Embedded SQLite with write-ahead logging (WAL) for high concurrency and zero corruption.
- Persistent memory storage (`memories` table) with importance ratings and keyword search.
- Recursive project scanner: indexes project files, counts lines of code, and detects languages while ignoring build artifacts.

### 6. Horror Audio Engine & Procedural DSP (`com.ghosttrace.audio`)
- Standard library `javax.sound.sampled` polyphonic audio mixer.
- Includes 20 original 44.1kHz 16-bit PCM WAV sound effects (ambient wind, dark hums, static bursts, whispers, keystroke haunting, guardian intercept/approval).
- Built-in pure Java DSP synthesizer (`AudioSynthesizer`) as a zero-asset fallback.
- `HorrorAudioDirector` enforces hierarchical priority: CRITICAL > IMPORTANT > ATMOSPHERIC > AMBIENT, suppressing audio in `FOCUS` mode.

### 7. Interactive Terminal UI & CLI (`com.ghosttrace.terminal`)
- Built on **JLine 3**, the standard Java terminal engine.
- Phosphor green ANSI CRT aesthetic (`#00ff66`), ASCII banners, box styling, tab-autocompletion, and persistent command history.
- Dual execution modes: interactive TUI shell or direct CLI execution.

---

## 3. Quick Start

### Prerequisites
- Java 21+ (OpenJDK 21, 22, 23, 24, or 25)
- Apache Maven 3.8+

### Building the Project
```bash
mvn clean package
```
This generates the standalone fat JAR at `target/ghosttrace-1.2.0.jar`.

### Launching the Interactive Haunted Terminal
```bash
./ghosttrace
```
or:
```bash
java -jar target/ghosttrace-1.2.0.jar
```

---

## 4. Command Reference

| Command | Usage | Description |
| :--- | :--- | :--- |
| `help` | `help [cmd]` | Display available commands and detailed usage. |
| `status` | `status` | Display full subsystem health and telemetry report. |
| `ghost` | `ghost [status\|profiles\|set <profile>\|mood <name>]` | Configure ghost persona and mood states. |
| `guard` | `guard [analyze <cmd>\|approve <token>\|run <cmd>]` | Analyze command safety, request HMAC token, or run safely. |
| `ai` | `ai [ask <query>\|status\|clear]` | Query the local AI reasoning engine. |
| `dev` | `dev [analyze\|review\|debug\|tests] <code>` | Static heuristics, code review, and debugging triage. |
| `memory` | `memory [list\|search <q>\|save <k> <v>\|delete <k>]` | Persistent SQLite memory CRUD. |
| `project` | `project [scan <path> [name]\|info]` | Scan and index local codebases. |
| `system` | `system [overview\|health]` | Host telemetry (CPU, RAM, Disk, Threads). |
| `haunt` | `haunt [mode <normal\|ambient\|haunted\|focus>]` | Switch atmospheric intensity modes. |
| `audio` | `audio [status\|volume <0-100>\|mute\|unmute\|test]` | Horror audio mixer controls and diagnostics. |
| `clear` | `clear` | Clear the terminal display. |
| `exit` | `exit` | Gracefully shut down GhostTrace. |

---

## 5. Non-Interactive CLI Mode Examples

GhostTrace can also be invoked as a standard CLI utility directly from bash or CI/CD pipelines:

```bash
# Check status
./ghosttrace status

# Static security analysis of a dangerous command
./ghosttrace guard analyze "rm -rf /"

# Ask a programming question
./ghosttrace ai ask "How do I avoid deadlocks in Java?"

# Static analysis of a code snippet
./ghosttrace dev analyze "public class Test { void run() { try {} catch(Exception e){} } }"

# Store persistent developer context
./ghosttrace memory save db_host "10.0.0.1"

# Telemetry and health diagnostics
./ghosttrace system health
```

---

## 6. Running Tests

Run the full automated test suite (36 unit tests covering all 10 subsystems):
```bash
mvn test
```
