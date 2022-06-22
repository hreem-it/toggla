package io.hreem.toggler.project.model;

import java.util.Date;
import java.util.UUID;

import lombok.Builder;

public record ApiKey(UUID apiKey, String description, Environment env, Date createdAt, Date updatedAt) {
    @Builder
    public ApiKey {
    }
}
