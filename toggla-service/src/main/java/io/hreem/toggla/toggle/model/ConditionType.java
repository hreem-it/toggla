package io.hreem.toggla.toggle.model;

public enum ConditionType {
    // Lexical comparisons
    CONTAINS,
    IS_BLANK,
    NOT_CONTAINS,
    IS_NOT_BLANK,
    // Numerical comparisons
    LESS_THAN,
    GREATER_THAN,
    NOT_LESS_THAN,
    NOT_GREATER_THAN,
    LESS_THAN_OR_EQUAL,
    GREATER_THAN_OR_EQUAL,
    NOT_LESS_THAN_OR_EQUAL,
    NOT_GREATER_THAN_OR_EQUAL,
    // Alphanumeric comparisons
    IN,
    EQUAL,
    NOT_IN,
    NOT_EQUAL,
    // Boolean comparisons
    IS_TRUE,
    IS_FALSE,
    // Date comparisons
    IS_DATE_AFTER,
    IS_DATE_BEFORE,
    IS_DATE_BETWEEN,
    IS_DATE_NOT_AFTER,
    IS_DATE_NOT_BEFORE,
    IS_DATE_NOT_BETWEEN,
}
