package io.hreem.toggler.examples;

import io.hreem.toggler.TogglerClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/fruit")
@ApplicationScoped
public class Resource {

    @Inject
    TogglerClient toggler;

    @GET
    public String get() {
        if (toggler.isFeatureEnabled("new-fruit-feature")) {
            return getNewFruit();
        } else if (toggler.isFeatureEnabled("new-fruit-feature", "variant-1")) {
            return getNewFruitVar1();
        }
        return getOldFruit();
    }

    private String getOldFruit() {
        return "Old Fruit";
    }

    private String getNewFruit() {
        return "New Shiny Fruit";
    }

    private String getNewFruitVar1() {
        return "New Shiny Fruit Var1";
    }

}
