package io.hreem.toggla.common.repository;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

public enum RepositoryTypes {
    REDIS("redis"),
    DDB("dynamodb");

    private final String name;

    RepositoryTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static RepositoryTypes fromString(String name) {
        for (RepositoryTypes repositoryTypes : RepositoryTypes.values()) {
            if (repositoryTypes.getName().equals(name)) {
                return repositoryTypes;
            }
        }
        throw new IllegalArgumentException("No enum constant " + name);
    }
}
