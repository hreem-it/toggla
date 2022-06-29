package io.hreem.toggler.examples;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "http://localhost:8080/toggles")
public interface TogglerClient {

    @Path("/{toggleKey}")
    @Retry(maxRetries = 4, delay = 100)
    @Fallback(fallbackMethod = "fallbackGetToggleStatus")
    Boolean getToggleStatus(@PathParam("toggleKey") String toggleKey, @HeaderParam("api-secret") String apiSecret);

    /**
     * Default to an unopened toggle if the service is unresponsive.
     * 
     * @param toggleKey the toggle key
     * @param apiSecret the api secret
     * @return the default toggle status
     */
    default Boolean fallbackGetToggleStatus(String toggleKey, String apiSecret) {
        return false;
    }
}
