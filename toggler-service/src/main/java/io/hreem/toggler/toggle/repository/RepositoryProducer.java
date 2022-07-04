package io.hreem.toggler.toggle.repository;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.literal.NamedLiteral;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.hreem.toggler.common.repository.Repository;
import io.hreem.toggler.toggle.model.Toggle;

@ApplicationScoped
public class RepositoryProducer {

    static enum RepositoryTypes {
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

    @Inject
    @ConfigProperty(name = "toggler.toggle.repository.type", defaultValue = "redis")
    String repositoryType;

    @Inject Instance<Repository<String, Toggle>> repositoryInstance;

    public Repository<String, Toggle> getRepository() {
        return repositoryInstance.select(NamedLiteral.of(repositoryType)).get();
    }
}
