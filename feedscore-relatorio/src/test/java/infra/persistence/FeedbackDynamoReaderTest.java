package infra.persistence;

import domain.models.FeedbackSummary;
import domain.repositories.FeedbackReader;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class FeedbackDynamoReaderTest {

    @Inject
    DynamoDbClient dynamoDbClient;

    @Inject
    FeedbackReader feedbackReader;

    @ConfigProperty(name = "feedscore.dynamodb.table-name")
    String tableName;

    @Test
    @DisplayName("Deve buscar pelo índice apenas os feedbacks da semana informada")
    void deveBuscarFeedbacksPelaSemana() {
        String descricaoDaSemana = "Feedback da semana " + UUID.randomUUID();
        String descricaoDeOutraSemana = "Feedback de outra semana " + UUID.randomUUID();

        salvarItem(descricaoDaSemana, 3, "CRITICAL", "2026-11-10T17:00:00Z", "2026-W46");
        salvarItem(descricaoDeOutraSemana, 9, "LOW", "2026-11-03T17:00:00Z", "2026-W45");

        List<FeedbackSummary> resultado = feedbackReader.findByWeek("2026-W46");

        List<FeedbackSummary> encontrados = resultado.stream()
                .filter(feedback -> feedback.description().equals(descricaoDaSemana))
                .toList();

        assertEquals(1, encontrados.size());
        assertEquals(3, encontrados.getFirst().score());
        assertTrue(resultado.stream().noneMatch(feedback -> feedback.description().equals(descricaoDeOutraSemana)));
    }

    private void salvarItem(String descricao, int nota, String urgencia, String createdAt, String semana) {
        Map<String, AttributeValue> item = Map.of(
                "id", AttributeValue.fromS(UUID.randomUUID().toString()),
                "description", AttributeValue.fromS(descricao),
                "score", AttributeValue.fromN(String.valueOf(nota)),
                "urgency", AttributeValue.fromS(urgencia),
                "createdAt", AttributeValue.fromS(createdAt),
                "semana", AttributeValue.fromS(semana)
        );

        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build());
    }
}