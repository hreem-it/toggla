package io.hreem.toggla.project.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.hreem.toggla.common.repository.Repository;
import io.hreem.toggla.project.model.ApiKey;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import io.hreem.toggla.common.repository.DBTypeQualifiers;
import io.hreem.toggla.common.repository.DataTypeQualifiers;
import io.hreem.toggla.common.repository.ObjectNotFoundException;

@ApplicationScoped
@DBTypeQualifiers.DynamoDB
@DataTypeQualifiers.APIKey
public class DynamoDBKeyRepository implements Repository<String, ApiKey> {

    private static final String TABLE_NAME = "toggla-keys";

    @Inject
    DynamoDbClient dynamoDB;

    @Override
    public List<String> getAllKeysMatching(String pattern) {
        final var requestBuilder = ScanRequest.builder()
                .tableName(TABLE_NAME);

        if (!pattern.equals("*") && !pattern.isEmpty()) {
            requestBuilder.filterExpression("apiKey = " + pattern);
        }

        return dynamoDB.scanPaginator(requestBuilder.build()).items().stream()
                .map(ApiKey::fromMap)
                .map(ApiKey::apiKey)
                .map(UUID::toString)
                .toList();
    }

    @Override
    public void create(String apiKey, ApiKey data) {
        final var request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(data.toMap())
                .build();
        dynamoDB.putItem(request);
    }

    @Override
    public void update(String apiKey, ApiKey data) {
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
                .key(Map.of("apiKey", AttributeValue.builder().s(id).build()))
                .build();
        dynamoDB.deleteItem(request);
    }

    @Override
    public ApiKey get(String id) throws ObjectNotFoundException {
        final var request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("apiKey", AttributeValue.builder().s(id).build()))
                .build();
        final var response = dynamoDB.getItem(request);
        if (response.item() == null) {
            throw new ObjectNotFoundException("Object with id " + id + " does not exist");
        }

        return ApiKey.fromMap(response.item());
    }

    @Override
    public boolean exists(String id) {
        final var request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("apiKey", AttributeValue.builder().s(id).build()))
                .build();
        final var response = dynamoDB.getItem(request);
        return response.item() != null;
    }

}
