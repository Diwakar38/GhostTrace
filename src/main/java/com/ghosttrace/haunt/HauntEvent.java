package com.ghosttrace.haunt;

import com.ghosttrace.ghost.GhostProfileId;
import java.util.List;
import java.util.Map;
import java.util.Random;

public record HauntEvent(
    String id,
    EventCategory category,
    String name,
    Map<GhostProfileId, List<String>> templates,
    int priority,
    long cooldownSeconds
) {
    private static final Random RANDOM = new Random();

    public String resolveText(GhostProfileId profileId) {
        List<String> list = templates.get(profileId);
        if (list == null || list.isEmpty()) {
            list = templates.get(GhostProfileId.FORGOTTEN_MACHINE);
        }
        if (list == null || list.isEmpty()) {
            return "░▒▓ Something stirs silently in the terminal...";
        }
        return list.get(RANDOM.nextInt(list.size()));
    }
}
