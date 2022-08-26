package io.hreem.toggla.toggle.model;

public record Conditional(
        String key,
        ConditionType type,
        String startingValue,
        String endingValue) {
}
