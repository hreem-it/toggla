package io.hreem.toggler.toggle.model.dto;

import javax.validation.constraints.NotBlank;

import lombok.Builder;

public record NewToggleRequest(
                @NotBlank(message = "Toggle must have a project unique key") String key,
                String description,
                boolean enabled) {

        @Builder
        public NewToggleRequest {

        }

}
