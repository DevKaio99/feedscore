package application.inputs;

import java.time.Instant;

public record FeedbackNotificationCreateInput (
        String description,
        String urgency,
        Instant createdAt) {
}
