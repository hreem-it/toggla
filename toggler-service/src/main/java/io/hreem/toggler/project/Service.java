package io.hreem.toggler.project;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;

import io.hreem.toggler.common.Util;
import io.hreem.toggler.project.model.ApiKey;
import io.hreem.toggler.project.model.Project;
import io.hreem.toggler.project.model.dto.CreateApiKeyRequest;
import io.hreem.toggler.project.model.dto.CreateProjectRequest;
import io.quarkus.redis.client.RedisClient;
import io.quarkus.redis.client.reactive.ReactiveRedisClient;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class Service {

    @Inject
    RedisClient redis;

    @Inject
    ReactiveRedisClient reactiveRedis;

    @Inject
    Util util;

    /**
     * Creates a new project.
     * 
     * @param request
     */
    public void createProject(@Valid CreateProjectRequest request) {
        // Check if project with key already exists
        if (redis.exists(List.of(request.projectKey())).toBoolean()) {
            throw new BadRequestException("Project with key " + request.projectKey() + " already exists");
        }

        // Create new project
        final var newProject = Project.builder()
                .projectKey(request.projectKey())
                .description(request.description())
                .createdAt(new Date())
                .updatedAt(new Date())
                .apiKeys(List.of())
                .build();
        redis.set(List.of("project:" + request.projectKey(), util.convert(newProject)));
    }

    /**
     * Creates an API key for a project.
     * 
     * @param projectKey The project key
     * @param request    The request containing description and environment config.
     */
    public UUID createAPIKey(String projectKey, @Valid CreateApiKeyRequest request) {
        // Check if project with key exists
        final var projectGetRequest = redis.get("project:" + projectKey);
        if (projectGetRequest == null) {
            throw new NotFoundException("Project with key " + projectKey + " does not exist");
        }
        final var project = util.convert(projectGetRequest.toString(), Project.class);

        // Check if API key with environment config already exists
        final var apiKeyWithSameEnvironment = project.apiKeys().stream()
                .filter(key -> key.env().equals(request.env()))
                .findFirst();
        if (apiKeyWithSameEnvironment.isPresent()) {
            throw new ForbiddenException("API key with environment " + request.env() + " already exists");
        }

        // Create new project api key
        final var newAPIKey = ApiKey.builder()
                .description(request.description())
                .apiKey(UUID.randomUUID())
                .createdAt(new Date())
                .updatedAt(new Date())
                .env(request.env())
                .build();
        project.apiKeys().add(newAPIKey);
        redis.set(List.of("project:" + projectKey, util.convert(project)));
        redis.set(List.of("apiKey:" + newAPIKey.apiKey(), projectKey));

        return newAPIKey.apiKey();
    }

    /**
     * Verifies if an API key is valid, and returns the project-key if it is.
     * 
     * @param apiKey
     * @return The project key if the API key is valid.
     */
    public String getProjectKeyFromApiKey(String apiKey) {
        final var projectKey = redis.get("apiKey:" + apiKey);
        if (projectKey == null) {
            throw new ForbiddenException("API key " + apiKey + " does not exist");
        }
        return projectKey.toString();
    }

    /**
     * Verifies if an API key is valid, and returns the project-key if it is.
     * 
     * @param apiKey
     * @return The project key if the API key is valid.
     */
    public Uni<String> getProjectKeyFromApiKeyAsync(String apiKey) {
        final var projectKey = reactiveRedis.get("apiKey:" + apiKey);
        if (projectKey == null) {
            throw new ForbiddenException("API key " + apiKey + " does not exist");
        }
        return projectKey.map(response -> response.toString());
    }

    /**
     * Returns the project with all it's API keys obfuscated.
     * 
     * @param projectKey The project key
     * @return The project with all it's API keys obfuscated.
     */
    public Project getProject(String projectKey, boolean obfuscate) {
        // Check if project with key exists
        final var projectGetRequest = redis.get("project:" + projectKey);
        if (projectGetRequest == null) {
            throw new NotFoundException("Project with key " + projectKey + " does not exist");
        }
        final var project = util.convert(projectGetRequest.toString(), Project.class);

        // Obfuscate all API keys
        if (obfuscate) {
            final var obfuscatedKeys = project.apiKeys().stream()
                    .map(key -> ApiKey.builder()
                            .apiKey(new UUID(0, 0))
                            .createdAt(key.createdAt())
                            .updatedAt(key.updatedAt())
                            .description(key.description())
                            .env(key.env())
                            .build())
                    .collect(Collectors.toList());
            project.apiKeys().clear();
            project.apiKeys().addAll(obfuscatedKeys);
        }

        return project;
    }

    public List<Project> getProjects() {
        final var projects = redis.keys("project:*").stream()
                .map(projectResponse -> projectResponse.toString())
                .map(projectKey -> projectKey.split("project:")[1])
                .parallel()
                .map(projectKey -> getProject(projectKey, true))
                .collect(Collectors.toList());

        return projects;
    }

    public String getProjectEnvironment(String projectKey, String apiKey) {
        final var project = getProject(projectKey, false);
        final var matchingKey = project.apiKeys().stream()
                .filter(key -> key.apiKey().equals(UUID.fromString(apiKey)))
                .findFirst()
                .orElseThrow(() -> new ForbiddenException("API key " + apiKey + " does not exist"));

        return matchingKey.env().toString();
    }

}
