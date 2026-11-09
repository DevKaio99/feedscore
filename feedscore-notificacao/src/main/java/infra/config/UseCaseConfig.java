package infra.config;

import application.gateways.EmailSender;
import application.usecases.NotifyCriticalFeedbackUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@ApplicationScoped

public class UseCaseConfig {

    @Produces
    @Singleton
    public NotifyCriticalFeedbackUseCase notifyCriticalFeedbackUseCase(EmailSender emailSender) {
        return new NotifyCriticalFeedbackUseCase(emailSender);
    }
}

