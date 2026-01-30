package com.example.graph.snapshot;

import java.time.OffsetDateTime;

public record TimeSlice(
        OffsetDateTime requestedAt,
        OffsetDateTime resolvedAt,
        String timezone
) {
}
