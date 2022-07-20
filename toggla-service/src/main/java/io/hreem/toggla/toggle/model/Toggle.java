package io.hreem.toggla.toggle.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.hreem.toggla.common.Util;
import io.hreem.toggla.project.model.Environment;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@RegisterForReflection
public record Toggle(
                String id,
                String toggleKey,
                String description,
                List<Variation> variations,
                String projectKey,
                Environment environment,
                Date createdAt,
                Date updatedAt) {

        private static final Util util = new Util();

        @Builder
        public Toggle {
        }

        public static Toggle fromMap(Map<String, AttributeValue> item) {
                return Toggle.builder()
                                .id(item.get("id").s())
                                .toggleKey(item.get("toggleKey").s())
                                .description(item.get("description") != null ? item.get("description").s() : "")
                                .projectKey(item.get("projectKey").s())
                                .environment(Environment.valueOf(item.get("environment").s()))
                                .variations(item.get("variations") != null ? item.get("variations").l().stream()
                                                .map(a -> Variation.fromMap((a.m())))
                                                .collect(Collectors.toList()) : List.of())
                                .createdAt(util.formatDate(item.get("createdAt").s()))
                                .updatedAt(util.formatDate(item.get("updatedAt").s()))
                                .build();
        }

        public Map<String, AttributeValue> toMap() {
                final var map = new HashMap<String, AttributeValue>();
                map.put("id", AttributeValue.builder().s(id).build());
                map.put("toggleKey", AttributeValue.builder().s(toggleKey).build());
                if (description != null)
                        map.put("description", AttributeValue.builder().s(description).build());
                map.put("projectKey", AttributeValue.builder().s(projectKey).build());
                map.put("environment", AttributeValue.builder().s(environment.name()).build());
                if (variations != null) {
                        map.put("variations", AttributeValue.builder().l(
                                        variations.stream()
                                                        .map(variation -> variation.toMap())
                                                        .map(variationMap -> AttributeValue.builder().m(variationMap)
                                                                        .build())
                                                        .collect(Collectors.toList()))
                                        .build());
                }
                map.put("createdAt", AttributeValue.builder().s(util.formatDate(createdAt)).build());
                map.put("updatedAt", AttributeValue.builder().s(util.formatDate(updatedAt)).build());
                return map;
        }
}
