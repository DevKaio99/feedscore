package application.usecases;

import application.gateways.EmailMessage;
import application.gateways.EmailSender;
import application.inputs.FeedbackNotificationCreateInput;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class NotifyCriticalFeedbackUseCase {

    private static final ZoneId ZONE_SAO_PAULO = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String SUBJECT = "Alerta de avaliação crítica!";

    private final EmailSender emailSender;

    public NotifyCriticalFeedbackUseCase(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void execute(FeedbackNotificationCreateInput feedbackNotificationCreateInput) {

        String sendDate = feedbackNotificationCreateInput.createdAt()
                .atZone(ZONE_SAO_PAULO)
                .format(DATE_FORMAT);

        String bodyMessage = """
                Descrição: %s
                Urgência: %s
                Enviado em: %s
                """.formatted(
                        feedbackNotificationCreateInput.description(),
                        feedbackNotificationCreateInput.urgency(),
                        sendDate);

        emailSender.send(new EmailMessage(SUBJECT, bodyMessage));

    }

}
