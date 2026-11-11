package infra.persistence;

import domain.enums.Urgency;
import domain.models.FeedbackSummary;
import domain.repositories.FeedbackReader;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class FeedbackDynamoReader implements FeedbackReader {

    private static final String INDEX_NAME = "semana-createdAt-index";

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public FeedbackDynamoReader(DynamoDbClient dynamoDbClient,
                                @ConfigProperty(name = "feedscore.dynamodb.table-name") String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
    }

    @Override
    public List<FeedbackSummary> findByWeek(String week) {
        QueryRequest request = QueryRequest.builder()
                .tableName(tableName)
                .indexName(INDEX_NAME)
                .keyConditionExpression("semana = :semana")
                .expressionAttributeValues(Map.of(":semana", AttributeValue.fromS(week)))
                .build();

        return dynamoDbClient.queryPaginator(request)
                .items()
                .stream()
                .map(this::toFeedbackSummary)
                .toList();
    }

    private FeedbackSummary toFeedbackSummary(Map<String, AttributeValue> item) {
        return new FeedbackSummary(
                item.get("description").s(),
                Integer.parseInt(item.get("score").n()),
                Urgency.valueOf(item.get("urgency").s()),
                Instant.parse(item.get("createdAt").s())
        );
    }
}