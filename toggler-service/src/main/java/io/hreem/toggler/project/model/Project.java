package io.hreem.toggler.project.model;

import java.util.Date;
import java.util.List;

import lombok.Builder;

public record Project(String projectKey, String description, List<ApiKey> apiKeys, Date createdAt, Date updatedAt) {
    @Builder
    public Project {
    }
}
