package io.hreem.toggla.common;


import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

@RegisterForReflection
public record Violation(String reason, String field) {
    @Builder
    public Violation {
    }
}
