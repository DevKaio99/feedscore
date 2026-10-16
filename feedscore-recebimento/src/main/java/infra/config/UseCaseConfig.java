package infra.config;

import application.gateways.CriticalFeedbackPublisher;
import application.usecases.SubmitFeedbackUseCase;
import domain.repositories.FeedbackRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@ApplicationScoped

public class UseCaseConfig {

    @Produces
    @Singleton
    public SubmitFeedbackUseCase submitFeedbackUseCase(FeedbackRepository feedbackRepository,
                                                       CriticalFeedbackPublisher criticalFeedbackPublisher) {
        return new SubmitFeedbackUseCase(feedbackRepository, criticalFeedbackPublisher);
    }
}

