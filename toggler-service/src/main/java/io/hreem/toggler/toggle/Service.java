package io.hreem.toggler.toggle;

import java.time.Instant;
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

import io.hreem.toggler.toggle.model.Toggle;
import io.hreem.toggler.toggle.model.Variation;
import io.hreem.toggler.toggle.model.dto.AddToggleVariationRequest;
import io.hreem.toggler.toggle.model.dto.NewToggleRequest;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.redis.client.RedisClient;
import io.quarkus.redis.client.reactive.ReactiveRedisClient;
import io.smallrye.mutiny.Uni;

@RequestScoped
public class Service {

    @Inject
    Util util;

    @Inject
    RedisClient redis;

    @Inject
    ReactiveRedisClient reactiveRedisClient;

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
    @CacheInvalidate(cacheName = "toggle")
    @CacheInvalidate(cacheName = "toggle-list")
    @CacheInvalidate(cacheName = "toggle-status")
    public void toggle(@CacheKey String projectKey, @CacheKey String key, @CacheKey String variationKey)
            throws JsonMappingException, JsonProcessingException {
        final var variationKeyOrDefault = variationKey != null ? variationKey : "default";

        // Find toggle
        final var toggle = getToggle(projectKey, key);
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
                .build();
        util.replaceIf(toggle.variations(), v -> v.variationKey().equals(variationKeyOrDefault), v -> toggledVariation);

        // Save toggle
        redis.set(List.of(projectKey + ":" + key, util.convert(toggle)));
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
    @CacheInvalidate(cacheName = "toggle")
    @CacheInvalidate(cacheName = "toggle-list")
    @CacheInvalidate(cacheName = "toggle-status")
    public void addToggleVariation(AddToggleVariationRequest request, @CacheKey String projectKey, @CacheKey String key)
            throws JsonMappingException, JsonProcessingException {
        // Find toggle
        final var toggle = getToggle(projectKey, key);
        if (toggle == null)
            throw new NotFoundException("Toggle with key " + key + " does not exist");

        // Verify no variation with the same key exists
        final var variationKeyAlreadyExists = toggle.variations().stream()
                .anyMatch(v -> v.variationKey().equals(request.variationKey()));
        if (variationKeyAlreadyExists)
            throw new BadRequestException("Toggle with key " + key + " and variation " + request.variationKey()
                    + " already exists");

        // Add new variation
        final var newVariation = Variation.builder()
                .variationKey(request.variationKey())
                .description(request.description())
                .enabled(request.enabled())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();
        toggle.variations().add(newVariation);

        // Save toggle
        redis.set(List.of(projectKey + ":" + key, util.convert(toggle)));
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
    @CacheInvalidate(cacheName = "toggle")
    @CacheInvalidate(cacheName = "toggle-list")
    @CacheInvalidate(cacheName = "toggle-status")
    public void removeToggleVariation(@CacheKey String projectKey, @CacheKey String key, String variationKey)
            throws JsonMappingException, JsonProcessingException {
        // Find toggle
        final var toggle = getToggle(projectKey, key);
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
        redis.set(List.of(projectKey + ":" + key, util.convert(toggle)));
    }

    /**
     * Creates a new toggle configuration and saves it to the datastore.
     * 
     * @param request    the new toggle configuration
     * @param projectKey the project key
     * @throws JsonProcessingException
     */
    @CacheInvalidate(cacheName = "toggle")
    @CacheInvalidate(cacheName = "toggle-list")
    @CacheInvalidate(cacheName = "toggle-status")
    public void createNewToggle(NewToggleRequest request, @CacheKey String projectKey) throws JsonProcessingException {
        // Create a new toggle configuration
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

        // Check that no toggle with the same key already exists
        if (redis.exists(List.of(projectKey + ":" + newToggle.key())).toBoolean())
            throw new BadRequestException("A toggle with the key " + newToggle.key() + " already exists");

        // Save the toggle configuration to Redis
        redis.set(List.of(projectKey + ":" + newToggle.key(), util.convert(newToggle)));
    }

    @CacheInvalidate(cacheName = "toggle")
    @CacheInvalidate(cacheName = "toggle-list")
    @CacheInvalidate(cacheName = "toggle-status")
    public void removeToggle(@CacheKey String projectKey, @CacheKey String key) {
        // Check that a toggle with the key exists
        if (redis.exists(List.of(projectKey + ":" + key)).toBoolean())
            redis.del(List.of(projectKey + ":" + key));
        throw new NotFoundException("No toggle with the key " + key + " exists");
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
    @CacheResult(cacheName = "toggle")
    public Toggle getToggle(@CacheKey String projectKey, @CacheKey String toggleKey)
            throws JsonMappingException, JsonProcessingException {
        // Get the toggle configuration from Redis
        final var getResponse = redis.get(projectKey + ":" + toggleKey);
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
    public List<Toggle> getAllToggles(@CacheKey String projectKey) {
        final var getAllResponse = redis.keys(projectKey + ":*");
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
     * Non blocking, get toggle status from the datastore for a given project key.
     * 
     * @param projectKey   The project key to get toggle status for.
     * @param key          The toggle key to get status for.
     * @param variationKey The variation key to get status for.
     * @return Uni of Boolean, resolves non-blockingly.
     */
    @CacheResult(cacheName = "toggle-status")
    public Uni<Boolean> getToggleStatus(@CacheKey String projectKey, @CacheKey String key,
            @CacheKey String variationKey) {
        final var variationKeyOrDefault = variationKey != null ? variationKey : "default";
        return reactiveRedisClient.get(projectKey + ":" + key)
                .map(response -> {
                    if (response == null)
                        throw new NotFoundException("Toggle with key " + key + " does not exist");
                    return util.convert(response.toString(), Toggle.class);
                })
                .map(toggle -> {
                    final var variation = toggle.variations().stream()
                            .filter(v -> v.variationKey().equals(variationKeyOrDefault))
                            .findFirst()
                            .orElseThrow(() -> new NotFoundException(
                                    "Toggle with key " + key + " and variation " + variationKeyOrDefault
                                            + " does not exist"));
                    return variation.enabled();
                });
    }
}
