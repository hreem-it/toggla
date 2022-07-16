package io.hreem.toggla.examples;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class TogglaService {

    @Inject
    @ConfigProperty(name = "toggla.api.secret")
    String apiSecret;

    @Inject
    @RestClient
    TogglaClient togglaClient;

    public boolean isOpened(String toggleKey) {
        return togglaClient.getToggleStatus(toggleKey, apiSecret);
    }

}
