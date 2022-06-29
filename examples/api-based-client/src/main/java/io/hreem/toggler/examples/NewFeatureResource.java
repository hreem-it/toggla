package io.hreem.toggler.examples;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import io.quarkus.logging.Log;

@Path("my-new-feature")
@ApplicationScoped
public class NewFeatureResource {

    @Inject
    TogglerService toggle;

    @Context
    HttpHeaders headers;

    @GET
    @Path("test")
    public boolean test() {
        Log.info(headers.getHeaderString("api-secret"));
        return true;
    }

    @GET
    public boolean getNewFeature() {
        if (toggle.isOpened("mock-toggle-key")) {
            return true;
        } else {
            return false;
        }
    }
}
