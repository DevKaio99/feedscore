package infra.messaging;

import application.gateways.CriticalFeedbackPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.entities.Feedback;
import infra.dtos.CriticalFeedbackMessageDTO;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@ApplicationScoped
public class SqsCriticalFeedbackPublisher implements CriticalFeedbackPublisher {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final String queueUrl;

    public SqsCriticalFeedbackPublisher(SqsClient sqsClient, ObjectMapper objectMapper, @ConfigProperty(name = "feedscore.sqs.critical-queue-url") String queueUrl) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
    }


    @Override
    public void publish(Feedback feedback) {
        CriticalFeedbackMessageDTO message = new CriticalFeedbackMessageDTO(
                feedback.getDescription(),
                feedback.getUrgency().getLabel(),
                feedback.getCreatedAt()
        );

        sqsClient.sendMessage(SendMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .messageBody(toJson(message))
                        .build());

    }

    private String toJson (CriticalFeedbackMessageDTO message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Falha ao serializar a mensagem do feedback");
        }
    }
}
