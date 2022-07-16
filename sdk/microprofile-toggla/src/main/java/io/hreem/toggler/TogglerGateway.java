package io.hreem.toggla;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("toggles")
@ApplicationScoped
@RegisterRestClient(configKey = "toggla")
@RegisterClientHeaders(AuthHeaderFactory.class)
@RegisterProvider(RequestExceptionMapper.class)
public interface TogglerGateway {

    @GET
    @Path("/{toggleKey}/status")
    @Retry(retryOn = RetryableRequestException.class, maxRetries = 4)
    Boolean getToggleStatus(@PathParam("toggleKey") String toggleKey);

    @GET
    @Path("/{toggleKey}/{variationKey}/status")
    @Retry(retryOn = RetryableRequestException.class, maxRetries = 4)
    Boolean getToggleVariationStatus(@PathParam("toggleKey") String toggleKey,
            @PathParam("variationKey") String variationKey);

}
