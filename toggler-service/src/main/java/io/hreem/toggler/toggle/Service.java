package io.hreem.toggler.toggle;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.hreem.toggler.toggle.model.Toggle;
import io.hreem.toggler.toggle.model.Variation;
import io.hreem.toggler.toggle.model.dto.NewToggleRequest;
import io.quarkus.redis.client.RedisClient;

@ApplicationScoped
public class Service {
    private static ObjectMapper mapper = new ObjectMapper();

    @Inject
    RedisClient redis;

    public void createNewToggle(NewToggleRequest request, String projectKey) throws JsonProcessingException {
        // Create a new toggle configuration
        final var newToggle = Toggle.builder()
                .key(request.key())
                .description(request.description())
                .variations(List.of(
                        Variation.builder()
                                .variationKey("default")
                                .enabled(request.defaultVariationEnabled())
                                .build()))
                .build();

        // Save the toggle configuration to Redis
        redis.set(List.of(projectKey + ":" + newToggle.key(), mapper.writeValueAsString(newToggle)));
    }

    public Toggle getToggle(String projectKey, String toggleKey) throws JsonMappingException, JsonProcessingException {
        // Get the toggle configuration from Redis
        final var getResponse = redis.get(projectKey + ":" + toggleKey).toString();
        final var toggle = mapper.readValue(getResponse, Toggle.class);
        return toggle;
    }
}
