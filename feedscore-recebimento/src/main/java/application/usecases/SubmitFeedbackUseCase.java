package application.usecases;

import application.gateways.CriticalFeedbackPublisher;
import application.inputs.FeedbackCreateInput;
import domain.entities.Feedback;
import domain.repositories.FeedbackRepository;

public class SubmitFeedbackUseCase {

    private final FeedbackRepository feedbackRepository;
    private final CriticalFeedbackPublisher criticalFeedbackPublisher;

    public SubmitFeedbackUseCase(FeedbackRepository feedbackRepository, CriticalFeedbackPublisher criticalFeedbackPublisher) {
        this.feedbackRepository = feedbackRepository;
        this.criticalFeedbackPublisher = criticalFeedbackPublisher;
    }

    public Feedback execute (FeedbackCreateInput feedbackInput) {

        Feedback feedback = Feedback.create(
                feedbackInput.description(),
                feedbackInput.score()
        );
        var feedbackSaved = feedbackRepository.saveFeedback(feedback);

        if (feedbackSaved.isCritical()) {
            criticalFeedbackPublisher.publish(feedbackSaved);
        }
        return feedbackSaved;
    }

}
