package infra.dtos;

import java.time.Instant;
import java.util.UUID;

public record FeedbackResponseDTO (
        UUID id,
        String description,
        int score,
        String urgency,
        Instant createdAt
) {
}
