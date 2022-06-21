package io.hreem.toggler.project.model;

import java.util.UUID;

import lombok.Builder;

public record ApiKey(UUID apiKey, String description, boolean active) {
    @Builder
    public ApiKey {
    }
}
