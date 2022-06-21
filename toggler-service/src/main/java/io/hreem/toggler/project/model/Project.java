package io.hreem.toggler.project.model;

import java.util.List;

import lombok.Builder;

public record Project(String projectName, Environment environment, List<ApiKey> apiKeys) {
    @Builder
    public Project {
    }
}
