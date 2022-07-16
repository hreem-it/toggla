package io.hreem.toggla.toggle.repository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.hreem.toggla.common.repository.Repository;
import io.hreem.toggla.toggle.model.Toggle;
import io.hreem.toggla.common.repository.DBTypeQualifiers;
import io.hreem.toggla.common.repository.DataTypeQualifiers;

@ApplicationScoped
public class RepositoryProducer {

    @Inject
    @ConfigProperty(name = "toggla.toggle.repository.type", defaultValue = "redis")
    String repositoryType;

    @Inject
    @DBTypeQualifiers.Redis
    @DataTypeQualifiers.Toggle
    Repository<String, Toggle> redisRepository;

    @Inject
    @DBTypeQualifiers.DynamoDB
    @DataTypeQualifiers.Toggle
    Repository<String, Toggle> ddbRepository;

    public Repository<String, Toggle> getRepository() {
        return switch (repositoryType) {
            case "redis" -> redisRepository;
            case "dynamodb" -> ddbRepository;
            default -> throw new IllegalArgumentException(
                    "Invalid DB type specified, allowed types are 'redis' or 'dynamodb'");
        };
    }
}
