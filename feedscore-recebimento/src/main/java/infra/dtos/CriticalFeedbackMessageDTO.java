package infra.dtos;

import java.time.Instant;

public record CriticalFeedbackMessageDTO(
        String description,
        String urgency,
        Instant createdAt
) {
}
