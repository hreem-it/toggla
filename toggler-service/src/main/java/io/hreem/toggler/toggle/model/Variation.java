package io.hreem.toggler.toggle.model;

import lombok.Builder;

public record Variation(String variationKey, String description, boolean enabled) {
    @Builder
    public Variation {
    
    }

    public Object toggle() {
        return null;
    }
}
