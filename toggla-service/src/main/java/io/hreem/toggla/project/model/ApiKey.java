package io.hreem.toggla.project.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.hreem.toggla.common.Util;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@RegisterForReflection
public record ApiKey(
        UUID apiKey,
        String description,
        Environment env,
        String projectKey,
        Date createdAt,
        Date updatedAt) {

    private static final Util util = new Util();

    @Builder
    public ApiKey {
    }

    public static ApiKey fromMap(Map<String, AttributeValue> item) {
        return ApiKey.builder()
                .apiKey(UUID.fromString(item.get("apiKey").s()))
                .description(item.get("description") != null ? item.get("description").s() : "")
                .env(Environment.valueOf(item.get("env").s()))
                .projectKey(item.get("projectKey").s())
                .createdAt(util.formatDate(item.get("createdAt").s()))
                .updatedAt(util.formatDate(item.get("updatedAt").s()))
                .build();
    }

    public Map<String, AttributeValue> toMap() {
        final var map = new HashMap<String, AttributeValue>();

        map.put("apiKey", AttributeValue.builder().s(apiKey.toString()).build());
        if (description != null)
            map.put("description", AttributeValue.builder().s(description).build());
        map.put("env", AttributeValue.builder().s(env.name()).build());
        map.put("projectKey", AttributeValue.builder().s(projectKey).build());
        map.put("createdAt", AttributeValue.builder().s(util.formatDate(createdAt)).build());
        map.put("updatedAt", AttributeValue.builder().s(util.formatDate(updatedAt)).build());
        return map;
    }

}
