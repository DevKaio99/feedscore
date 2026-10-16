package infra.persistence;

import domain.entities.Feedback;
import domain.repositories.FeedbackRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;

@ApplicationScoped
public class FeedbackDynamoRepository implements FeedbackRepository {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public FeedbackDynamoRepository(DynamoDbClient dynamoDbClient, @ConfigProperty(name = "feedscore.dynamodb.table-name") String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
    }



    @Override
    public Feedback saveFeedback(Feedback feedback) {
        Map<String, AttributeValue> item = Map.of(
                "id", AttributeValue.fromS(feedback.getId().toString()),
                "description", AttributeValue.fromS(feedback.getDescription()),
                "score", AttributeValue.fromN(String.valueOf(feedback.getScore())),
                "urgency", AttributeValue.fromS(feedback.getUrgency().name()),
                "createdAt", AttributeValue.fromS(feedback.getCreatedAt().toString())
        );

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .conditionExpression("attribute_not_exists(id)")
                .build();

        dynamoDbClient.putItem(request);

        return feedback;
    }
}
