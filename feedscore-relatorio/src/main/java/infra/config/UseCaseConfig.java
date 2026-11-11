package infra.config;

import application.gateways.EmailSender;
import application.usecases.WeeklyReportCreateUseCase;
import domain.repositories.FeedbackReader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import java.time.Clock;

@ApplicationScoped
public class UseCaseConfig {

    @Produces
    @Singleton
    public WeeklyReportCreateUseCase weeklyReportCreateUseCase(FeedbackReader feedbackReader, EmailSender emailSender, Clock clock) {
        return new WeeklyReportCreateUseCase(feedbackReader, emailSender, clock);
    }

    @Produces
    @Singleton
    public Clock clock() {
        return Clock.systemUTC();
    }
}
