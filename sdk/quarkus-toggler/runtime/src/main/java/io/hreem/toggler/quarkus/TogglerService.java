package io.hreem.toggler.quarkus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

@Path("toggles")
@RegisterClientHeaders(AuthHeaderFactory.class)
public interface TogglerService {

    @GET
    @Path("/{toggleKey}/status")
    @Retry(retryOn = RetryableRequestException.class, maxRetries = 4)
    Boolean getToggleStatus(String toggleKey);

    @GET
    @Path("/{toggleKey}/{variationKey}/status")
    @Retry(retryOn = RetryableRequestException.class, maxRetries = 4)
    Boolean getToggleVariationStatus(String toggleKey, String variationKey);

}
