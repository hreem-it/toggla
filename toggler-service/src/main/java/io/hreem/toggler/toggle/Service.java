package io.hreem.toggler.toggle;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.hreem.toggler.toggle.model.Toggle;
import io.hreem.toggler.toggle.model.Variation;
import io.hreem.toggler.toggle.model.dto.AddToggleVariationRequest;
import io.hreem.toggler.toggle.model.dto.NewToggleRequest;
import io.quarkus.redis.client.RedisClient;

@RequestScoped
public class Service {

    @Inject
    Util util;

    @Inject
    RedisClient redis;

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
    public void toggle(String projectKey, String key, String variationKey)
            throws JsonMappingException, JsonProcessingException {
        final var variationKeyOrDefault = variationKey != null ? variationKey : "default";

        // Find toggle
        final var toggle = getToggle(projectKey, key);
        if (toggle == null)
            throw new IllegalArgumentException("Toggle with key " + key + " does not exist");

        // Find variation
        final var optVariationToToggle = toggle.variations().stream()
                .filter(v -> v.variationKey().equals(variationKeyOrDefault))
                .findFirst();
        final var variation = optVariationToToggle
                .orElseThrow(() -> new IllegalArgumentException(
                        "Toggle with key " + key + " and variation " + variationKeyOrDefault + " does not exist"));

        // Toggle variation
        final var toggledVariation = Variation.builder()
                .variationKey(variationKeyOrDefault)
                .enabled(!variation.enabled())
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
    public void addToggleVariation(AddToggleVariationRequest request, String projectKey, String key)
            throws JsonMappingException, JsonProcessingException {
        // Find toggle
        final var toggle = getToggle(projectKey, key);
        if (toggle == null)
            throw new IllegalArgumentException("Toggle with key " + key + " does not exist");

        // Verify no variation with the same key exists
        final var variationKeyAlreadyExists = toggle.variations().stream()
                .anyMatch(v -> v.variationKey().equals(request.variationKey()));
        if (variationKeyAlreadyExists)
            throw new IllegalArgumentException("Toggle with key " + key + " and variation " + request.variationKey()
                    + " already exists");

        // Add new variation
        final var newVariation = Variation.builder()
                .variationKey(request.variationKey())
                .description(request.description())
                .enabled(request.enabled())
                .build();
        toggle.variations().add(newVariation);

        // Save toggle
        redis.set(List.of(projectKey + ":" + key, util.convert(toggle)));
    }

    public void removeToggleVariation(String projectKey, String key, String variationKey) {

    }

    /**
     * Creates a new toggle configuration and saves it to the datastore.
     * 
     * @param request    the new toggle configuration
     * @param projectKey the project key
     * @throws JsonProcessingException
     */
    public void createNewToggle(NewToggleRequest request, String projectKey) throws JsonProcessingException {
        // Create a new toggle configuration
        final var newToggle = Toggle.builder()
                .key(request.key())
                .description(request.description())
                .variations(List.of(
                        Variation.builder()
                                .variationKey("default")
                                .enabled(request.enabled())
                                .build()))
                .build();

        // Check that no toggle with the same key already exists
        if (redis.exists(List.of(projectKey + ":" + newToggle.key())).toBoolean())
            throw new IllegalArgumentException("A toggle with the key " + newToggle.key() + " already exists");

        // Save the toggle configuration to Redis
        redis.set(List.of(projectKey + ":" + newToggle.key(), util.convert(newToggle)));
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
    public Toggle getToggle(String projectKey, String toggleKey) throws JsonMappingException, JsonProcessingException {
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
    public List<Toggle> getAllToggles(String projectKey) {
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
}
