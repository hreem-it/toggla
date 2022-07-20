package io.hreem.toggla.project.repository;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.hreem.toggla.common.repository.Repository;
import io.hreem.toggla.project.model.Project;
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
@DataTypeQualifiers.Project
public class DynamoDBProjectRepository implements Repository<String, Project> {

    private static final String TABLE_NAME = "toggla-projects";

    @Inject
    DynamoDbClient dynamoDB;

    @Override
    public List<String> getAllKeysMatching(String pattern) {
        final var requestBuilder = ScanRequest.builder()
                .tableName(TABLE_NAME);

        if (!pattern.equals("*") && !pattern.isEmpty()) {
            requestBuilder.filterExpression("projectKey = " + pattern);
        }

        return dynamoDB.scanPaginator(requestBuilder.build()).items().stream()
                .map(Project::fromMap)
                .map(Project::projectKey)
                .toList();
    }

    @Override
    public void create(String id, Project data) {
        final var request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(data.toMap())
                .build();
        dynamoDB.putItem(request);
    }

    @Override
    public void update(String id, Project data) {
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
                .key(Map.of("projectKey", AttributeValue.builder().s(id).build()))
                .build();
        dynamoDB.deleteItem(request);
    }

    @Override
    public Project get(String id) throws ObjectNotFoundException {
        final var request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("projectKey", AttributeValue.builder().s(id).build()))
                .build();
        final var response = dynamoDB.getItem(request);
        if (response.item().isEmpty()) {
            throw new ObjectNotFoundException("Object with id " + id + " does not exist");
        }

        return Project.fromMap(response.item());
    }

    @Override
    public boolean exists(String id) {
        final var request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("projectKey", AttributeValue.builder().s(id).build()))
                .build();
        final var response = dynamoDB.getItem(request);
        return !response.item().isEmpty();
    }

}
