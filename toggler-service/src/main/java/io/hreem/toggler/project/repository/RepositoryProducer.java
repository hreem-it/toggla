package io.hreem.toggler.project.repository;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.literal.NamedLiteral;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.hreem.toggler.common.repository.Repository;
import io.hreem.toggler.project.model.Project;
import io.hreem.toggler.common.repository.DBTypeQualifiers;
import io.hreem.toggler.common.repository.DataTypeQualifiers;

@ApplicationScoped
public class RepositoryProducer {

    @Inject
    @ConfigProperty(name = "toggler.toggle.repository.type", defaultValue = "redis")
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
    Repository<String, String> redisKeyRepository;

    @Inject
    @DBTypeQualifiers.DynamoDB
    @DataTypeQualifiers.APIKey
    Repository<String, String> ddbKeyRepository;

    public Repository<String, Project> getProjectRepository() {
        return switch (repositoryType) {
            case "redis" -> redisProjectRepository;
            case "dynamodb" -> ddbProjectRepository;
            default -> throw new IllegalArgumentException(
                    "Invalid DB type specified, allowed types are 'redis' or 'dynamodb'");
        };
    }

    public Repository<String, String> getKeyRepository() {
        return switch (repositoryType) {
            case "redis" -> redisKeyRepository;
            case "dynamodb" -> ddbKeyRepository;
            default -> throw new IllegalArgumentException(
                    "Invalid DB type specified, allowed types are 'redis' or 'dynamodb'");
        };
    }
}
