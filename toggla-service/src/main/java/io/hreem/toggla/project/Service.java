package io.hreem.toggla.project;

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

import io.hreem.toggla.common.Util;
import io.hreem.toggla.common.repository.ObjectNotFoundException;
import io.hreem.toggla.common.repository.Repository;
import io.hreem.toggla.project.model.ApiKey;
import io.hreem.toggla.project.model.Project;
import io.hreem.toggla.project.model.dto.CreateApiKeyRequest;
import io.hreem.toggla.project.model.dto.CreateProjectRequest;
import io.hreem.toggla.project.repository.RepositoryProducer;
import io.quarkus.redis.client.RedisClient;
import io.quarkus.redis.client.reactive.ReactiveRedisClient;

@ApplicationScoped
public class Service {

    @Inject
    RedisClient redis;

    @Inject
    ReactiveRedisClient reactiveRedis;

    @Inject
    Util util;

    Repository<String, Project> projectRepository;
    Repository<String, String> keyRepository;

    public Service(RepositoryProducer repoProducer) {
        this.projectRepository = repoProducer.getProjectRepository();
        this.keyRepository = repoProducer.getKeyRepository();
    }

    /**
     * Creates a new project.
     * 
     * @param request
     */
    public void createProject(@Valid CreateProjectRequest request) {
        // Check if project with key already exists
        if (redis.exists(List.of("project:" + request.projectKey())).toBoolean()) {
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
        projectRepository.create("project:" + request.projectKey(), newProject);
    }

    /**
     * Creates an API key for a project.
     * 
     * @param projectKey The project key
     * @param request    The request containing description and environment config.
     */
    public UUID createAPIKey(String projectKey, @Valid CreateApiKeyRequest request) {
        // Check if project with key exists
        try {
            final var project = projectRepository.get("project:" + projectKey);

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
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException("Project with key " + projectKey + " does not exist");
        }
    }

    /**
     * Verifies if an API key is valid, and returns the project-key if it is.
     * 
     * @param apiKey
     * @return The project key if the API key is valid.
     */
    public String getProjectKeyFromApiKey(String apiKey) {
        try {
            final var projectKey = keyRepository.get("apiKey:" + apiKey);
            return projectKey;
        } catch (ObjectNotFoundException e) {
            throw new ForbiddenException("API key " + apiKey + " does not exist");
        }
    }

    /**
     * Returns the project with all it's API keys obfuscated.
     * 
     * @param projectKey The project key
     * @return The project with all it's API keys obfuscated.
     */
    public Project getProject(String projectKey, boolean obfuscate) {
        // Check if project with key exists
        try {
            final var project = projectRepository.get("project:" + projectKey);

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
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException("Project with key " + projectKey + " does not exist");
        }
    }

    public List<Project> getProjects() {
        final var projects = projectRepository.getAllKeysMatching("project:*")
                .stream()
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
