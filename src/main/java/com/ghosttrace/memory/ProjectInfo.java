package com.ghosttrace.memory;

import java.time.Instant;
import java.util.List;

public record ProjectInfo(
    long id,
    String name,
    String path,
    String languages,
    int totalFiles,
    long totalLines,
    Instant scannedAt
) {}
