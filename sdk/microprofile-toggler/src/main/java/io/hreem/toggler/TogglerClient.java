package io.hreem.toggler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TogglerClient {

    @Inject
    TogglerService poller;

    public boolean isFeatureEnabled(String toggleKey) {
        return poller.fetch(toggleKey, null);
    }

    public boolean isFeatureEnabled(String toggleKey, String variationKey) {
        return poller.fetch(toggleKey, variationKey);
    }
}
