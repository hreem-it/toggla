package io.hreem.toggla.project.repository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.hreem.toggla.common.repository.Repository;
import io.hreem.toggla.project.model.ApiKey;
import io.hreem.toggla.project.model.Project;
import io.hreem.toggla.common.repository.DBTypeQualifiers;
import io.hreem.toggla.common.repository.DataTypeQualifiers;

@ApplicationScoped
public class RepositoryProducer {

    @Inject
    @ConfigProperty(name = "toggla.repository.type", defaultValue = "redis")
    String repositoryType;

    @Inject
    @DBTypeQualifiers.Redis
    @DataTypeQualifiers.Project
    Repository<String, Project> redisProjectRepository;

    @Inject
    @DBTypeQualifiers.DynamoDB
    @DataTypeQualifiers.Project
    Repository<String, Project> ddbProjectRepository;

    @Inject
    @DBTypeQualifiers.Redis
    @DataTypeQualifiers.APIKey
    Repository<String, ApiKey> redisKeyRepository;

    @Inject
    @DBTypeQualifiers.DynamoDB
    @DataTypeQualifiers.APIKey
    Repository<String, ApiKey> ddbKeyRepository;

    public Repository<String, Project> getProjectRepository() {
        return switch (repositoryType) {
            case "redis" -> redisProjectRepository;
            case "dynamodb" -> ddbProjectRepository;
            default -> throw new IllegalArgumentException(
                    "Invalid DB type specified, allowed types are 'redis' or 'dynamodb'");
        };
    }

    public Repository<String, ApiKey> getKeyRepository() {
        return switch (repositoryType) {
            case "redis" -> redisKeyRepository;
            case "dynamodb" -> ddbKeyRepository;
            default -> throw new IllegalArgumentException(
                    "Invalid DB type specified, allowed types are 'redis' or 'dynamodb'");
        };
    }
}
