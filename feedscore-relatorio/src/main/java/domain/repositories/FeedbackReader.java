package domain.repositories;

import domain.models.FeedbackSummary;

import java.util.List;

public interface FeedbackReader {
    List<FeedbackSummary> findByWeek (String week);
}
