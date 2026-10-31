package com.ghosttrace.memory;

import java.time.Instant;
import java.util.List;

public record MemoryItem(
    long id,
    String key,
    String content,
    String category,
    String tags,
    int importance,
    Instant createdAt
) {}
