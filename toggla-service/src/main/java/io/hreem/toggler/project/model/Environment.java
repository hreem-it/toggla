package io.hreem.toggla.project.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public enum Environment {
    DEV,
    TEST,
    PROD,
    CANARY,
}
