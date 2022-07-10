package io.hreem.toggler.quarkus;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TogglerClient {

    @Inject
    Poller poller;

    public boolean isFeatureEnabled(String toggleKey) {
        return poller.fetch(toggleKey, null);
    }

    public boolean isFeatureEnabled(String toggleKey, String variationKey) {
        return poller.fetch(toggleKey, variationKey);
    }

}
