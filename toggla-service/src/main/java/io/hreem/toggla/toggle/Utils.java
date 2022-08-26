package io.hreem.toggla.toggle;

import io.hreem.toggla.common.Violation;
import io.hreem.toggla.toggle.model.dto.ConditionalRequest;
import io.hreem.toggla.toggle.model.dto.NewToggleRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    public static void validateRequest(NewToggleRequest request) {
        final var conditionals = request.conditionals();

        final var violations = conditionals.stream()
                .map(Utils::validateConditional)
                .flatMap(List::stream)
                .collect(Collectors.toList());

    }

    private static List<Violation> validateConditional(ConditionalRequest conditional) {
        final var violations = new ArrayList<Violation>();
        switch (conditional.type()) {
            // Lexical Conditions
            case CONTAINS,
                IS_BLANK,
                NOT_CONTAINS,
                IS_NOT_BLANK -> {
               if(conditional.startingValue() == null)
                   violations.add(Violation.builder()
                           .field("startingValue")
                           .reason("Value cannot be null for lexical conditional")
                           .build());
                if(conditional.endingValue() != null)
                    violations.add(Violation.builder()
                            .field("endingValue")
                            .reason("Value not allowed for lexical conditional")
                            .build());
            }
            // Numerical conditions
            case LESS_THAN,
                GREATER_THAN,
                NOT_LESS_THAN,
                NOT_GREATER_THAN,
                LESS_THAN_OR_EQUAL,
                GREATER_THAN_OR_EQUAL,
                NOT_LESS_THAN_OR_EQUAL,
                NOT_GREATER_THAN_OR_EQUAL -> {
                try {
                    Double.valueOf(conditional.startingValue());
                } catch (NumberFormatException ex) {
                    violations.add(Violation.builder()
                            .field("startingValue")
                            .reason("Value must be a number for a numerical conditional")
                            .build());
                }
                if(conditional.endingValue() != null)
                    violations.add(Violation.builder()
                            .field("endingValue")
                            .reason("Value not allowed for numerical conditional")
                            .build());
            }
        };

        return violations;
    }
}
