package io.hreem.toggler.project.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public enum Environment {
    DEV,
    TEST,
    PROD,
    CANARY,
}
