package io.hreem.toggler.toggle.model;

import java.util.Date;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

@RegisterForReflection
public record Variation(String variationKey, String description, boolean enabled, Date createdAt,
        Date updatedAt) {
    @Builder
    public Variation {
    }
}
