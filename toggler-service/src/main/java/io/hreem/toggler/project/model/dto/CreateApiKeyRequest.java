package io.hreem.toggler.project.model.dto;

import io.hreem.toggler.project.model.Environment;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

@RegisterForReflection
public record CreateApiKeyRequest(String description, Environment env) {
    @Builder
    public CreateApiKeyRequest {
    }
}
