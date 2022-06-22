package io.hreem.toggler.toggle.model;

import java.util.Date;

import lombok.Builder;

public record Variation(String variationKey, String description, boolean enabled, Date createdAt,
        Date updatedAt) {
    @Builder
    public Variation {
    }
}
