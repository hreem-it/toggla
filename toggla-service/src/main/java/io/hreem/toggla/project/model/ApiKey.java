package io.hreem.toggla.project.model;

import java.util.Date;
import java.util.UUID;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

@RegisterForReflection
public record ApiKey(UUID apiKey, String description, Environment env, Date createdAt, Date updatedAt) {
    @Builder
    public ApiKey {
    }
}
