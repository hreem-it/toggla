package io.hreem.toggler.examples;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("my-new-feature")
@ApplicationScoped
public class NewFeatureResource {

    @Inject
    TogglerService toggle;

    @GET
    public boolean getNewFeature() {
        if (toggle.isOpened("mock-toggle-key")) {
            return true;
        } else {
            return false;
        }
    }
}
