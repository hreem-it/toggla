package io.hreem.toggler.toggle.model.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

@RegisterForReflection
public record AddToggleVariationRequest(String variationKey, String description, boolean enabled) {
    @Builder
    public AddToggleVariationRequest {
    }
}
