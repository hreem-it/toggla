package io.hreem.toggla.project.model.dto;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

@RegisterForReflection
public record CreateProjectRequest(
        @Length(min = 4, max = 25) @Pattern(regexp = "^([a-z][a-z0-9]*)(-[a-z0-9]+)*$", message = "Project key must be written in kebab case, e.g compass or my-fruit-store") String projectKey,
        String description) {
    @Builder
    public CreateProjectRequest {
    }
}
