package com.ghosttrace.terminal;

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

public record CommandContext(
    Settings settings,
    GhostEngine ghostEngine,
    GuardianEngine guardianEngine,
    MemoryEngine memoryEngine,
    AIEngine aiEngine,
    DeveloperEngine developerEngine,
    SystemEngine systemEngine,
    HauntEngine hauntEngine,
    AudioManager audioManager,
    HorrorAudioDirector horrorDirector
) {}
