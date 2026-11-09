package infra.dtos;

import java.time.Instant;

public record CriticalFeedbackMessageRequestDTO(
        String description,
        String urgency,
        Instant createdAt) {
}
