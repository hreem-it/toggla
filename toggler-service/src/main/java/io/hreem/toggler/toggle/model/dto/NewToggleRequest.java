package io.hreem.toggler.toggle.model.dto;

import javax.validation.constraints.NotBlank;

public record NewToggleRequest(
        @NotBlank(message = "Toggle must have a project unique key") String key,
        String description,
        boolean defaultVariationEnabled) {

}
