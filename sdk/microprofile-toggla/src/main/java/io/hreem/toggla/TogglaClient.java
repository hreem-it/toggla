package io.hreem.toggla;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TogglaClient {

    @Inject
    TogglaService poller;

    public boolean isFeatureEnabled(String toggleKey) {
        return poller.fetch(toggleKey, null);
    }

    public boolean isFeatureEnabled(String toggleKey, String variationKey) {
        return poller.fetch(toggleKey, variationKey);
    }
}
