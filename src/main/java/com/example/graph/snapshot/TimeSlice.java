package com.example.graph.snapshot;

import java.time.OffsetDateTime;

public class TimeSlice {

    private final OffsetDateTime requestedAt;
    private final OffsetDateTime resolvedAt;
    private final String timezone;

    public TimeSlice(
            OffsetDateTime requestedAt,
            OffsetDateTime resolvedAt,
            String timezone
    ) {
        this.requestedAt = requestedAt;
        this.resolvedAt = resolvedAt;
        this.timezone = timezone;
    }

    public OffsetDateTime getRequestedAt() {
        return requestedAt;
    }

    public OffsetDateTime getResolvedAt() {
        return resolvedAt;
    }

    public String getTimezone() {
        return timezone;
    }
}
