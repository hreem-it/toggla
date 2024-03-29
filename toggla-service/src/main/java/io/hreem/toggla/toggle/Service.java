package io.hreem.toggla.toggle;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.hreem.toggla.common.Util;
import io.hreem.toggla.common.http.RequestContext;
import io.hreem.toggla.common.repository.ObjectNotFoundException;
import io.hreem.toggla.common.repository.Repository;
import io.hreem.toggla.project.model.Environment;
import io.hreem.toggla.toggle.model.Toggle;
import io.hreem.toggla.toggle.model.Variation;
import io.hreem.toggla.toggle.model.dto.AddToggleVariationRequest;
import io.hreem.toggla.toggle.model.dto.NewToggleRequest;
import io.hreem.toggla.toggle.repository.RepositoryProducer;

@RequestScoped
public class Service {

    @Inject
    Util util;

    @Inject
    RequestContext context;

    Repository<String, Toggle> repository;

    public Service(RepositoryProducer repoProducer) {
        this.repository = repoProducer.getRepository();
    }

    /**
     * Toggle an existing feature-toggle.
     * 
     * @param projectKey   The project key.
     * @param key          The feature-toggle key.
     * @param variationKey The variation key.
     * @throws JsonMappingException    If the feature-toggle or variation key is
     *                                 invalid.
     * @throws JsonProcessingException If the feature-toggle or variation key is
     *                                 invalid.
     */
    public void toggle(String key, String variationKey)
            throws JsonMappingException, JsonProcessingException {
        final var projectKey = context.getProjectKey();
        final var environment = context.getEnvironment();
        final var variationKeyOrDefault = variationKey != null ? variationKey : "default";

        // Find toggle
        final var toggle = getToggle(key);
        if (toggle == null)
            throw new NotFoundException("Toggle with key " + key + " does not exist");

        // Find variation
        final var optVariationToToggle = toggle.variations().stream()
                .filter(v -> v.variationKey().equals(variationKeyOrDefault))
                .findFirst();
        final var variation = optVariationToToggle
                .orElseThrow(() -> new NotFoundException(
                        "Toggle with key " + key + " and variation " + variationKeyOrDefault + " does not exist"));

        // Toggle variation
        final var toggledVariation = Variation.builder()
                .variationKey(variationKeyOrDefault)
                .enabled(!variation.enabled())
                .createdAt(variation.createdAt())
                .updatedAt(new Date())
                .description(variation.description())
                .build();
        util.replaceIf(toggle.variations(), v -> v.variationKey().equals(variationKeyOrDefault), v -> toggledVariation);

        // Save toggle
        final var id = util.constructKey(projectKey, environment, key);

        try {
            repository.update(id, toggle);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException("Toggle with key " + key + " does not exist");
        }
    }

    /**
     * Create a new feature-toggle variation.
     * 
     * @param request    The new feature-toggle variation request.
     * @param projectKey The project key.
     * @param key        The feature-toggle key.
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    public void addToggleVariation(AddToggleVariationRequest request, String key)
            throws JsonMappingException, JsonProcessingException {
        final var projectKey = context.getProjectKey();

        // Create new variation
        final var newVariation = Variation.builder()
                .variationKey(request.variationKey())
                .description(request.description())
                .enabled(request.enabled())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        for (var environment : Environment.values()) {
            // Find toggle
            final var toggle = getToggle(key, environment.toString());
            if (toggle == null)
                throw new NotFoundException("Toggle with key " + key + " does not exist");

            // Verify no variation with the same key exists
            final var variationKeyAlreadyExists = toggle.variations().stream()
                    .anyMatch(v -> v.variationKey().equals(request.variationKey()));
            if (variationKeyAlreadyExists)
                throw new BadRequestException("Toggle with key " + key + " and variation " + request.variationKey()
                        + " already exists");

            // Save toggle
            toggle.variations().add(newVariation);
            final var id = util.constructKey(projectKey, environment.toString(), key);

            repository.create(id, toggle);
        }
    }

    /**
     * Remove a feature-toggle variation.
     * 
     * @param projectKey   The project key.
     * @param key          The feature-toggle key.
     * @param variationKey The variation key.
     * @throws JsonProcessingException If the feature-toggle or variation key is
     * @throws JsonMappingException    If the feature-toggle or variation key is
     */
    public void removeToggleVariation(String key, String variationKey)
            throws JsonMappingException, JsonProcessingException {
        final var projectKey = context.getProjectKey();
        final var environment = context.getEnvironment();
        // Find toggle
        final var toggle = getToggle(key);
        if (toggle == null)
            throw new NotFoundException("Toggle with key " + key + " does not exist");

        // Verify variation exists
        final var optVariationToRemove = toggle.variations().stream()
                .filter(v -> v.variationKey().equals(variationKey))
                .findFirst();
        final var variation = optVariationToRemove
                .orElseThrow(() -> new NotFoundException(
                        "Toggle with key " + key + " and variation " + variationKey + " does not exist"));

        // Remove variation
        toggle.variations().remove(variation);

        // Save toggle
        final var id = util.constructKey(projectKey, environment, key);

        try {
            repository.update(id, toggle);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException("Toggle with key " + key + " does not exist");
        }
    }

    /**
     * Creates a new toggle configuration and saves it to the datastore.
     * 
     * @param request    the new toggle configuration
     * @param projectKey the project key
     * @throws JsonProcessingException
     */
    public void createNewToggle(NewToggleRequest request) throws JsonProcessingException {
        // Create a new toggle configuration
        final var projectKey = context.getProjectKey();
        final var environment = Environment.valueOf(context.getEnvironment());

        // Save the toggle configuration , one for each environment
        for (var env : Environment.values()) {
            // Check that no toggle with the same key already exists
            final var id = util.constructKey(projectKey, env.toString(), request.key());

            if (repository.exists(id))
                throw new BadRequestException("A toggle with the key " + request.key() + " already exists");

            // Save the toggle configuration , one for each environment
            final var newToggle = Toggle.builder()
                    .id(id)
                    .toggleKey(request.key())
                    .description(request.description())
                    .projectKey(projectKey)
                    .environment(environment)
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .variations(List.of(
                            Variation.builder()
                                    .variationKey("default")
                                    .enabled(request.enabled())
                                    .createdAt(new Date())
                                    .updatedAt(new Date())
                                    .build()))
                    .build();
            repository.create(id, newToggle);
        }
    }

    public void removeToggle(String key) {
        // Check that a toggle with the key exists
        final var projectKey = context.getProjectKey();
        for (var env : Environment.values()) {
            final var id = util.constructKey(projectKey, env.toString(), key);

            try {
                repository.delete(id);
            } catch (ObjectNotFoundException e) {
                throw new NotFoundException("Toggle with key " + key + " does not exist");
            }
        }
    }

    public Toggle getToggle(String toggleKey) {
        final var environment = context.getEnvironment();
        return getToggle(toggleKey, environment);
    }

    /**
     * Gets a toggle configuration from the datastore.
     * 
     * @param projectKey The project key
     * @param toggleKey  The toggle key
     * @return Toggle or null if the toggle does not exist
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public Toggle getToggle(String toggleKey, String environment) {
        // Get the toggle configuration
        final var projectKey = context.getProjectKey();
        final var id = util.constructKey(projectKey, environment, toggleKey);
        try {
            return repository.get(id);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets all toggle configurations from the datastore for a given project key.
     * 
     * @param projectKey The project key to get toggles for.
     * @return List of toggles.
     */
    public List<Toggle> getAllToggles() {
        final var projectKey = context.getProjectKey();
        final var environment = context.getEnvironment();
        final var idPattern = util.constructKey(projectKey, environment, "*");

        final var keys = repository.getAllKeysMatching(idPattern);
        final var toggles = keys.stream()
                .parallel()
                .map(t -> {
                    try {
                        return repository.get(t);
                    } catch (ObjectNotFoundException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return toggles;
    }

    /***
     * Get toggle status from the datastore for a given project key.
     * 
     * @param projectKey   The project key to get toggle status for.
     * @param key          The toggle key to get status for.
     * @param variationKey The variation key to get status for.
     * @return Uni of Boolean, resolves non-blockingly.
     */
    public Boolean getToggleStatus(String key, String variationKey) {
        final var variationKeyOrDefault = variationKey != null ? variationKey : "default";
        final var projectKey = context.getProjectKey();
        final var environment = context.getEnvironment();
        final var id = util.constructKey(projectKey, environment, key);

        try {
            final var toggle = repository.get(id);
            final var variation = toggle.variations().stream()
                    .filter(v -> v.variationKey().equals(variationKeyOrDefault))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(
                            "Toggle with key " + key + " and variation " + variationKeyOrDefault
                                    + " does not exist"));
            return variation.enabled();
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException("Toggle with key " + key + " does not exist");
        }

    }
}
