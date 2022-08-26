package io.hreem.toggla.toggle.model.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;

import io.hreem.toggla.toggle.model.ConditionType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.common.constraint.NotNull;
import lombok.Builder;

@RegisterForReflection
public record NewToggleRequest(
                @NotBlank(message = "Toggle must have a project unique key") String key,
                List<ConditionalRequest> conditionals,
                String description,
                boolean enabled) {

        @Builder
        public NewToggleRequest {

        }

}

@RegisterForReflection
public final record ConditionalRequest(
                @NotBlank(message = "Condition must have a key") String key,
                @NotNull ConditionType type,
                @NotBlank(message = "Condition must have a value") String startingValue,
                String endingValue) {
}
