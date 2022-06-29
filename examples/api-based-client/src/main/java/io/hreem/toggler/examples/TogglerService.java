package io.hreem.toggler.examples;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class TogglerService {

    @Inject
    @ConfigProperty(name = "toggler.api.secret")
    String apiSecret;

    @Inject
    @RestClient
    TogglerClient togglerClient;

    public boolean isOpened(String toggleKey) {
        return togglerClient.getToggleStatus(toggleKey, apiSecret);
    }

}
