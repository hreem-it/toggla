package io.hreem.toggler.toggle.model.dto;

import javax.validation.constraints.NotBlank;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

@RegisterForReflection
public record NewToggleRequest(
                @NotBlank(message = "Toggle must have a project unique key") String key,
                String description,
                boolean enabled) {

        @Builder
        public NewToggleRequest {

        }

}
