package io.hreem.toggla.toggle.repository;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.hreem.toggla.common.repository.Repository;
import io.hreem.toggla.toggle.model.Toggle;
import io.quarkus.logging.Log;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import io.hreem.toggla.common.repository.DBTypeQualifiers;
import io.hreem.toggla.common.repository.DataTypeQualifiers;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import io.hreem.toggla.common.repository.ObjectNotFoundException;

@ApplicationScoped
@DBTypeQualifiers.DynamoDB
@DataTypeQualifiers.Toggle
public class DynamoDBRepository implements Repository<String, Toggle> {

    private static final String TABLE_NAME = "toggla-toggles";

    @Inject
    DynamoDbClient dynamoDB;

    @Override
    public List<String> getAllKeysMatching(String pattern) {
        final var requestBuilder = ScanRequest.builder()
                .tableName(TABLE_NAME);

        if (!pattern.isEmpty()) {
            final var expr = pattern.split("\\*")[0];
            requestBuilder.filterExpression("contains(id, :pattern)")
                    .expressionAttributeValues(Map.of(":pattern", AttributeValue.builder().s(expr).build()));
        }

        return dynamoDB.scanPaginator(requestBuilder.build()).items().stream()
                .map(Toggle::fromMap)
                .map(Toggle::id)
                .toList();
    }

    @Override
    public void create(String id, Toggle data) {
        final var request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(data.toMap())
                .build();
        dynamoDB.putItem(request);
    }

    @Override
    public void update(String id, Toggle data) {
        final var request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(data.toMap())
                .build();
        dynamoDB.putItem(request);
    }

    @Override
    public void delete(String id) {
        final var request = DeleteItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("id", AttributeValue.builder().s(id).build()))
                .build();
        dynamoDB.deleteItem(request);
    }

    @Override
    public Toggle get(String id) throws ObjectNotFoundException {
        final var request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("id", AttributeValue.builder().s(id).build()))
                .build();
        final var response = dynamoDB.getItem(request);
        if (response.item().isEmpty()) {
            throw new ObjectNotFoundException("Object with id " + id + " does not exist");
        }

        return Toggle.fromMap(response.item());
    }

    @Override
    public boolean exists(String id) {
        Log.info("Checking if object with id " + id + " exists");
        final var request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("id", AttributeValue.builder().s(id).build()))
                .build();
        final var response = dynamoDB.getItem(request);
        return !response.item().isEmpty();
    }

}
