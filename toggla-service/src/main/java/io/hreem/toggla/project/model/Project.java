package io.hreem.toggla.project.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.hreem.toggla.common.Util;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@RegisterForReflection
public record Project(String projectKey, String description, List<ApiKey> apiKeys, Date createdAt, Date updatedAt) {

        private static final Util util = new Util();

        @Builder
        public Project {
        }

        public static Project fromMap(Map<String, AttributeValue> item) {
                return Project.builder()
                                .projectKey(item.get("projectKey").s())
                                .description(item.get("description") != null ? item.get("description").s() : "")
                                .apiKeys(item.get("apiKeys") != null ? item.get("apiKeys").l().stream()
                                                .map(atr -> ApiKey.fromMap(atr.m()))
                                                .collect(Collectors.toList()) : List.of())
                                .createdAt(util.formatDate(item.get("createdAt").s()))
                                .updatedAt(util.formatDate(item.get("updatedAt").s()))
                                .build();
        }

        public Map<String, AttributeValue> toMap() {
                final var map = new HashMap<String, AttributeValue>();
                map.put("projectKey", AttributeValue.builder().s(projectKey).build());
                if (description != null)
                        map.put("description", AttributeValue.builder().s(description).build());
                if (apiKeys != null) {
                        map.put("apiKeys", AttributeValue.builder().l(
                                        apiKeys.stream()
                                                        .map(key -> key.toMap())
                                                        .map(keyMap -> AttributeValue.builder().m(keyMap).build())
                                                        .collect(Collectors.toList()))
                                        .build());
                }
                map.put("createdAt", AttributeValue.builder().s(util.formatDate(createdAt)).build());
                map.put("updatedAt", AttributeValue.builder().s(util.formatDate(updatedAt)).build());
                return map;
        }
}
