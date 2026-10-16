package domain.entities;

import domain.enums.Urgency;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Feedback {

    private static final int NOTA_MINIMA = 0;
    private static final int NOTA_MAXIMA = 10;

    private final UUID id;
    private final String description;
    private final int score;
    private final Urgency urgency;
    private final Instant createdAt;

    private Feedback(UUID id, String description, int score, Instant createdAt) {
        Objects.requireNonNull(id, "O id é obrigatório");
        Objects.requireNonNull(createdAt, "A data de envio é obrigatória");

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A descrição é obrigatória");
        }
        if (score < NOTA_MINIMA || score > NOTA_MAXIMA) {
            throw new IllegalArgumentException("A nota deve estar entre 0 e 10");
        }

        this.id = id;
        this.description = description.strip();
        this.score = score;
        this.urgency = Urgency.fromScore(score);
        this.createdAt = createdAt;
    }

    public boolean isCritical() {
        return urgency == Urgency.CRITICAL;
    }

    public static Feedback create(String description, int score) {
        return new Feedback(UUID.randomUUID(), description, score, Instant.now());
    }

    public static Feedback reconstitute(UUID id, String description, int score, Instant createdAt) {
        return new Feedback(id, description, score, createdAt);
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getScore() {
        return score;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
