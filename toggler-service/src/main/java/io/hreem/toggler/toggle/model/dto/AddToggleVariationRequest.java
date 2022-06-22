package io.hreem.toggler.toggle.model.dto;

import lombok.Builder;

public record AddToggleVariationRequest(String variationKey, String description, boolean enabled) {
    @Builder
    public AddToggleVariationRequest {
    }
}
