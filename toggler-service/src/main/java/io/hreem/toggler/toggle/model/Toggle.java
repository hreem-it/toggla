package io.hreem.toggler.toggle.model;

import java.util.List;
import lombok.Builder;

public record Toggle(String key, String description, List<Variation> variations) {
    @Builder
    public Toggle {
    }
}
