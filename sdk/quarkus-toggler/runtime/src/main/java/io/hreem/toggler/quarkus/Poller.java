package io.hreem.toggler.quarkus;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class Poller {

    final Map<String, Boolean> toggleStatusCache = new HashMap<>();

    @Inject
    @ConfigProperty(name = "quarkus.toggler.base-uri")
    String baseUri;

    private TogglerService togglerService;

    @PostConstruct
    void init() {
        togglerService = RestClientBuilder.newBuilder()
                .baseUri(URI.create(baseUri))
                .register(RequestExceptionMapper.class)
                .build(TogglerService.class);
    }

    @Scheduled(every = "{quarkus.toggler.auto-toggle-refresh-rate}")
    void scheduledPoller() {
        // Fetch the keys from the client
        Set<String> toggleKeys = this.toggleStatusCache.keySet();

        // Fetch the status for each key
        for (String toggleKey : toggleKeys) {
            final var keyParts = toggleKey.split(":");
            var status = false;
            if (keyParts.length == 1)
                status = renew(keyParts[0], null);
            else
                status = renew(keyParts[0], keyParts[1]);
            this.toggleStatusCache.put(toggleKey, status);
        }
    }

    boolean fetch(String key, String variationKey) {
        final var toggleKey = constructKey(key, variationKey);
        var currentStatus = toggleStatusCache.get(toggleKey);
        if (currentStatus == null) {
            boolean status = false;
            try {
                if (variationKey != null) {
                    status = togglerService.getToggleVariationStatus(key, variationKey);
                } else {
                    status = togglerService.getToggleStatus(key);
                }
                toggleStatusCache.put(toggleKey, status);
                Log.debug("Polled toggle " + toggleKey + " for the first time with status " + status);
                return status;
            } catch (Exception e) {
                Log.error("Failed to fetch toggle from Toggler API, falling back to 'false'", e);
                return false;
            }
        }
        return currentStatus;
    }

    boolean renew(String key, String variationKey) {
        final var toggleKey = constructKey(key, variationKey);
        boolean status = false;
        try {
            if (variationKey != null) {
                status = togglerService.getToggleVariationStatus(key, variationKey);
            } else {
                status = togglerService.getToggleStatus(key);
            }
            Log.debug("Renewed toggle " + toggleKey + " with status " + status);
            return status;
        } catch (Exception e) {
            Log.error("Failed to fetch toggle from Toggler API, falling back to 'false'", e);
            return false;
        }

    }

    private String constructKey(String key, String variationKey) {
        return variationKey != null ? key + ":" + variationKey : key;
    }

}
