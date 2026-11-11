package domain.models;

import domain.enums.Urgency;

import java.time.Instant;

public record FeedbackSummary(
        String description,
        int score,
        Urgency urgency,
        Instant createdAt
) {
}
