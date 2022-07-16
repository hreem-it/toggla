package io.hreem.toggla;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MultivaluedMap;

@ApplicationScoped
public class AuthHeaderFactory implements ClientHeadersFactory {

    @Inject
    @ConfigProperty(name = "toggla.api-key")
    String apiKey;

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders,
            MultivaluedMap<String, String> clientOutgoingHeaders) {
        clientOutgoingHeaders.add("X-api-secret", apiKey);
        return clientOutgoingHeaders;
    }

}
