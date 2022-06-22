package io.hreem.toggler.toggle;

import static org.assertj.core.api.Assertions.*;

import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.hreem.toggler.toggle.model.Toggle;
import io.hreem.toggler.toggle.model.Variation;
import io.hreem.toggler.toggle.model.dto.AddToggleVariationRequest;
import io.hreem.toggler.toggle.model.dto.NewToggleRequest;
import io.quarkus.redis.client.RedisClient;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ToggleServiceTest {

    @Inject
    Service toggleService;

    @Inject
    RedisClient redis;

    @BeforeEach
    public void setup() {
        redis.flushall(List.of());
    }

    @Test
    public void toggle_should_fail_if_no_toggle_exists_with_the_key_or_variation_key() throws JsonProcessingException {
        // Given
        final var mockProjectKey = "mock-project-key";
        final var mockToggleKey = "mock-toggle-key";
        final var mockVariationKey = "mock-variation-key";

        // When + Then
        assertThatThrownBy(() -> toggleService.toggle(mockProjectKey, mockToggleKey, mockVariationKey))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Toggle with key " + mockToggleKey + " does not exist");

        // --------------------------------------------------

        // Given
        final var mockToggle = NewToggleRequest.builder()
                .key(mockToggleKey)
                .description("mock-description")
                .enabled(true)
                .build();
        toggleService.createNewToggle(mockToggle, mockProjectKey);

        // When + Then
        assertThatThrownBy(() -> toggleService.toggle(mockProjectKey, mockToggleKey, mockVariationKey))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(
                        "Toggle with key " + mockToggleKey + " and variation " + mockVariationKey + " does not exist");
    }

    @Test
    public void toggle_should_toggle_default_variation() throws JsonProcessingException {
        // Given
        final var mockProjectKey = "mock-project-key";
        final var mockToggleKey = "mock-toggle-key";
        final var mockToggle = NewToggleRequest.builder()
                .key(mockToggleKey)
                .description("mock-description")
                .enabled(false)
                .build();
        toggleService.createNewToggle(mockToggle, mockProjectKey);

        // When 1
        toggleService.toggle(mockProjectKey, mockToggleKey, null);
        final var toggle = toggleService.getToggle(mockProjectKey, mockToggleKey);

        // When 2
        toggleService.toggle(mockProjectKey, mockToggleKey, "default"); // This time we pass in a variation key.
        final var toggle2 = toggleService.getToggle(mockProjectKey, mockToggleKey);

        // Then 1
        assertThat(toggle).isNotNull();
        assertThat(toggle.variations()).hasSize(1);
        assertThat(toggle.variations().get(0).variationKey()).isEqualTo("default");
        assertThat(toggle.variations().get(0).enabled()).isTrue();

        // Then 2
        assertThat(toggle2).isNotNull();
        assertThat(toggle2.variations()).hasSize(1);
        assertThat(toggle2.variations().get(0).variationKey()).isEqualTo("default");
        assertThat(toggle2.variations().get(0).enabled()).isFalse();
    }

    @Test
    public void addToggleVariation_should_create_if_new_and_fail_if_toggle_variation_already_exists()
            throws JsonProcessingException {
        // Given
        final var mockProjectKey = "mock-project-key";
        final var mockToggleKey = "mock-toggle-key";
        final var mockVariationKey = "mock-toggle-variation-key";
        final var mockToggle = NewToggleRequest.builder()
                .key(mockToggleKey)
                .description("mock-description")
                .enabled(false)
                .build();
        toggleService.createNewToggle(mockToggle, mockProjectKey);

        final var variationRequest = AddToggleVariationRequest.builder()
                .variationKey(mockVariationKey)
                .enabled(true)
                .build();

        // When 1
        toggleService.addToggleVariation(variationRequest, mockProjectKey, mockToggleKey);
        final var toggle = toggleService.getToggle(mockProjectKey, mockToggleKey);

        // Then 1
        assertThat(toggle).isNotNull();
        assertThat(toggle.variations()).hasSize(2);
        toggle.variations().sort(Comparator.comparing(Variation::variationKey));
        assertThat(toggle.variations().get(0).variationKey()).isEqualTo("default");
        assertThat(toggle.variations().get(0).enabled()).isFalse();
        assertThat(toggle.variations().get(1).variationKey()).isEqualTo(mockVariationKey);
        assertThat(toggle.variations().get(1).enabled()).isTrue();

        // When 2 + Then 2
        assertThatThrownBy(() -> toggleService.addToggleVariation(variationRequest, mockProjectKey, mockToggleKey))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    public void removeToggleVariation_should_remove_if_toggle_with_variation_exists_and_fail_if_not()
            throws JsonMappingException, JsonProcessingException {
        // Given
        final var mockProjectKey = "mock-project-key";
        final var mockToggleKey = "mock-toggle-key";
        final var mockVariationKey = "mock-toggle-variation-key";
        final var mockToggle = NewToggleRequest.builder()
                .key(mockToggleKey)
                .description("mock-description")
                .enabled(false)
                .build();
        toggleService.createNewToggle(mockToggle, mockProjectKey);

        final var variationRequest = AddToggleVariationRequest.builder()
                .variationKey(mockVariationKey)
                .enabled(true)
                .build();
        toggleService.addToggleVariation(variationRequest, mockProjectKey, mockToggleKey);

        // When 1
        toggleService.removeToggleVariation(mockProjectKey, mockToggleKey, mockVariationKey);
        final var toggle = toggleService.getToggle(mockProjectKey, mockToggleKey);

        // Then 1
        assertThat(toggle).isNotNull();
        assertThat(toggle.variations()).hasSize(1);
        assertThat(toggle.variations().get(0).variationKey()).isEqualTo("default");

        // When + Then 2
        assertThatThrownBy(() -> toggleService.removeToggleVariation(mockProjectKey, mockToggleKey, mockVariationKey))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    public void createNewToggle_should_fail_if_toggle_with_key_already_exists() throws JsonProcessingException {
        // Given
        final var mockProjectKey = "mock-project-key";
        final var mockToggle = NewToggleRequest.builder()
                .key("mock-key")
                .description("mock-description")
                .enabled(true)
                .build();
        toggleService.createNewToggle(mockToggle, mockProjectKey);

        // When + Then
        assertThatThrownBy(() -> toggleService.createNewToggle(mockToggle, mockProjectKey))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    public void removeToggle_TODO() {
        // TODO
    }

    @Test
    public void getToggle_should_return_toggle_if_exists() throws JsonProcessingException {
        // Given
        final var mockProjectKey = "mock-project-key";
        final var mockToggle = NewToggleRequest.builder()
                .key("mock-key")
                .description("mock-description")
                .enabled(true)
                .build();

        toggleService.createNewToggle(mockToggle, mockProjectKey);

        // When
        final var response = toggleService.getToggle(mockProjectKey, mockToggle.key());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.key()).isEqualTo(mockToggle.key());
        assertThat(response.variations().get(0).variationKey()).isEqualTo("default");
        assertThat(response.variations().get(0).enabled()).isEqualTo(true);
    }

    @Test
    public void getToggle_should_return_null_if_non_existant() throws JsonProcessingException {
        // Given
        final var mockProjectKey = "mock-project-key";
        final var mockToggleKey = "mock-key";

        // When
        final var response = toggleService.getToggle(mockProjectKey, mockToggleKey);

        // Then
        assertThat(response).isNull();
    }

    @Test
    public void getAllToggles_should_return_all_created_toggles() throws JsonProcessingException {
        // Given
        final var mockProjectKey = "mock-project-key";
        final var mockToggle = NewToggleRequest.builder()
                .key("mock-key")
                .description("mock-description")
                .enabled(true)
                .build();
        final var mockToggle2 = NewToggleRequest.builder()
                .key("mock-key-2")
                .description("mock-description-2")
                .enabled(false)
                .build();

        toggleService.createNewToggle(mockToggle, mockProjectKey);
        toggleService.createNewToggle(mockToggle2, mockProjectKey);

        // When
        final var response = toggleService.getAllToggles(mockProjectKey);

        // Then
        assertThat(response).isNotNull();
        assertThat(response).hasSize(2);

        response.sort(Comparator.comparing(Toggle::key));
        assertThat(response.get(0).key()).isEqualTo("mock-key");
        assertThat(response.get(0).variations().get(0).variationKey()).isEqualTo("default");
        assertThat(response.get(0).variations().get(0).enabled()).isEqualTo(true);
        assertThat(response.get(1).key()).isEqualTo("mock-key-2");
        assertThat(response.get(1).variations().get(0).variationKey()).isEqualTo("default");
        assertThat(response.get(1).variations().get(0).enabled()).isEqualTo(false);
    }

    @Test
    public void getAllToggles_should_return_empty_list_if_new_project() throws JsonProcessingException {
        // Given
        final var mockProjectKey = "mock-project-key";

        // When
        final var response = toggleService.getAllToggles(mockProjectKey);

        // Then
        assertThat(response).isNotNull();
        assertThat(response).hasSize(0);
    }

    @Test
    public void getToggleStatus_TODO() {
        // TODO
    }

}
