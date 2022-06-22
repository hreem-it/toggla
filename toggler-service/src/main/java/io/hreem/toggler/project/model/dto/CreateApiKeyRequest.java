package io.hreem.toggler.project.model.dto;

import io.hreem.toggler.project.model.Environment;
import lombok.Builder;

public record CreateApiKeyRequest(String description, Environment env) {
    @Builder
    public CreateApiKeyRequest {
    }
}
