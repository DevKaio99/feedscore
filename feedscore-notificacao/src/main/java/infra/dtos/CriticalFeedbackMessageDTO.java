package infra.dtos;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.Instant;

@RegisterForReflection
public record CriticalFeedbackMessageDTO(
        String description,
        String urgency,
        Instant createdAt) {
}
