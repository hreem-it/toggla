package io.hreem.toggla.project.model.dto;

import io.hreem.toggla.project.model.Environment;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

@RegisterForReflection
public record CreateApiKeyRequest(String description, Environment env) {
    @Builder
    public CreateApiKeyRequest {
    }
}
