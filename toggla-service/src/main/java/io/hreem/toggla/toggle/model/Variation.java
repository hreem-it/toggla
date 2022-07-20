package io.hreem.toggla.toggle.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.hreem.toggla.common.Util;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@RegisterForReflection
public record Variation(String variationKey, String description, boolean enabled, Date createdAt,
        Date updatedAt) {

    private static final Util util = new Util();

    @Builder
    public Variation {
    }

    public static Variation fromMap(Map<String, AttributeValue> item) {
        return Variation.builder()
                .variationKey(item.get("variationKey").s())
                .description(item.get("description") != null ? item.get("description").s() : "")
                .enabled(item.get("enabled").bool())
                .createdAt(util.formatDate(item.get("createdAt").s()))
                .updatedAt(util.formatDate(item.get("updatedAt").s()))
                .build();
    }

    public Map<String, AttributeValue> toMap() {
        final var map = new HashMap<String, AttributeValue>();
        map.put("variationKey", AttributeValue.builder().s(variationKey).build());
        if (description != null)
            map.put("description", AttributeValue.builder().s(description).build());
        map.put("enabled", AttributeValue.builder().bool(enabled).build());
        map.put("createdAt", AttributeValue.builder().s(util.formatDate(createdAt)).build());
        map.put("updatedAt", AttributeValue.builder().s(util.formatDate(updatedAt)).build());
        return map;
    }

}
