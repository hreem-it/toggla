package io.hreem.toggler.toggle.model;

import java.util.Date;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

@RegisterForReflection
public record Toggle(String key, String description, List<Variation> variations, Date createdAt, Date updatedAt) {
    @Builder
    public Toggle {
    }
}
