package domain.repositories;

import domain.entities.Feedback;

public interface FeedbackRepository {
    Feedback saveFeedback (Feedback feedback);
}
