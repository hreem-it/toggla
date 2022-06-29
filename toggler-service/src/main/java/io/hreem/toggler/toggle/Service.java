package io.hreem.toggler.toggle;

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

import io.hreem.toggler.common.RequestContext;
import io.hreem.toggler.common.Util;
import io.hreem.toggler.project.model.Environment;
import io.hreem.toggler.toggle.model.Toggle;
import io.hreem.toggler.toggle.model.Variation;
import io.hreem.toggler.toggle.model.dto.AddToggleVariationRequest;
import io.hreem.toggler.toggle.model.dto.NewToggleRequest;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.logging.Log;
import io.quarkus.redis.client.RedisClient;

@RequestScoped
public class Service {

    @Inject
    Util util;

    @Inject
    RedisClient redis;

    @Inject
    RequestContext context;

    @Inject
    io.hreem.toggler.project.Service projectService;

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
    @CacheInvalidateAll(cacheName = "toggle")
    @CacheInvalidateAll(cacheName = "toggle-list")
    @CacheInvalidateAll(cacheName = "toggle-status")
    public void toggle(@CacheKey String key, @CacheKey String variationKey)
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
        Log.info(id);
        redis.set(List.of(id, util.convert(toggle)));
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
    @CacheInvalidateAll(cacheName = "toggle")
    @CacheInvalidateAll(cacheName = "toggle-list")
    @CacheInvalidateAll(cacheName = "toggle-status")
    public void addToggleVariation(AddToggleVariationRequest request, @CacheKey String key)
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
            Log.info(id);
            redis.set(List.of(id, util.convert(toggle)));
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
    @CacheInvalidateAll(cacheName = "toggle")
    @CacheInvalidateAll(cacheName = "toggle-list")
    @CacheInvalidateAll(cacheName = "toggle-status")
    public void removeToggleVariation(@CacheKey String key, @CacheKey String variationKey)
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
        Log.info(id);
        redis.set(List.of(id, util.convert(toggle)));
    }

    /**
     * Creates a new toggle configuration and saves it to the datastore.
     * 
     * @param request    the new toggle configuration
     * @param projectKey the project key
     * @throws JsonProcessingException
     */
    @CacheInvalidateAll(cacheName = "toggle")
    @CacheInvalidateAll(cacheName = "toggle-list")
    @CacheInvalidateAll(cacheName = "toggle-status")
    public void createNewToggle(NewToggleRequest request) throws JsonProcessingException {
        // Create a new toggle configuration
        final var projectKey = context.getProjectKey();
        final var newToggle = Toggle.builder()
                .key(request.key())
                .description(request.description())
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

        // Save the toggle configuration to Redis, one for each environment
        for (var env : Environment.values()) {
            // Check that no toggle with the same key already exists
            final var id = util.constructKey(projectKey, env.toString(), newToggle.key());
            Log.info(id);
            if (redis.exists(List.of(id)).toBoolean())
                throw new BadRequestException("A toggle with the key " + newToggle.key() + " already exists");

            // Save the toggle configuration to Redis, one for each environment
            redis.set(List.of(id, util.convert(newToggle)));
        }
    }

    @CacheInvalidateAll(cacheName = "toggle")
    @CacheInvalidateAll(cacheName = "toggle-list")
    @CacheInvalidateAll(cacheName = "toggle-status")
    public void removeToggle(@CacheKey String key) {
        // Check that a toggle with the key exists
        final var projectKey = context.getProjectKey();
        for (var env : Environment.values()) {
            final var id = util.constructKey(projectKey, env.toString(), key);
            Log.info(id);
            if (redis.exists(List.of(id)).toBoolean()) {
                redis.del(List.of(id));
            } else {
                throw new NotFoundException("Toggle with key " + key + " does not exist");
            }
        }
    }

    public Toggle getToggle(String toggleKey) throws JsonMappingException, JsonProcessingException {
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
    // @CacheResult(cacheName = "toggle")
    public Toggle getToggle(@CacheKey String toggleKey, @CacheKey String environment)
            throws JsonMappingException, JsonProcessingException {
        // Get the toggle configuration from Redis
        final var projectKey = context.getProjectKey();
        final var id = util.constructKey(projectKey, environment, toggleKey);
        Log.info(id);
        final var getResponse = redis.get(id);
        if (getResponse == null)
            return null;

        final var toggle = util.convert(getResponse.toString(), Toggle.class);
        return toggle;
    }

    /**
     * Gets all toggle configurations from the datastore for a given project key.
     * 
     * @param projectKey The project key to get toggles for.
     * @return List of toggles.
     */
    @CacheResult(cacheName = "toggle-list")
    public List<Toggle> getAllToggles() {
        final var projectKey = context.getProjectKey();
        final var environment = context.getEnvironment();
        final var id = util.constructKey(projectKey, environment, "*");
        Log.info(id);
        final var getAllResponse = redis.keys(id);
        final var toggles = getAllResponse.stream()
                .map(toggleKeyResponse -> toggleKeyResponse.toString())
                .parallel()
                .map(toggleKey -> redis.get(toggleKey))
                .filter(Objects::nonNull)
                .map(toggleResponse -> util.convert(toggleResponse.toString(), Toggle.class))
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
    @CacheResult(cacheName = "toggle-status")
    public Boolean getToggleStatus(@CacheKey String key,
            @CacheKey String variationKey) {
        final var variationKeyOrDefault = variationKey != null ? variationKey : "default";
        final var projectKey = context.getProjectKey();
        final var environment = context.getEnvironment();
        final var id = util.constructKey(projectKey, environment, key);
        Log.info(id);
        final var response = redis.get(id);
        if (response == null)
            throw new NotFoundException("Toggle with key " + key + " does not exist");

        final var toggle = util.convert(response.toString(), Toggle.class);

        final var variation = toggle.variations().stream()
                .filter(v -> v.variationKey().equals(variationKeyOrDefault))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "Toggle with key " + key + " and variation " + variationKeyOrDefault
                                + " does not exist"));
        return variation.enabled();
    }
}
