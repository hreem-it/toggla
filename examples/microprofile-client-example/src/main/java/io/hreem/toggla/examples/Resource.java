package io.hreem.toggla.examples;

import io.hreem.toggla.TogglaClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/fruit")
@ApplicationScoped
public class Resource {

    @Inject
    TogglaClient toggla;

    @GET
    public String get() {
        if (toggla.isFeatureEnabled("new-fruit-feature")) {
            return getNewFruit();
        } else if (toggla.isFeatureEnabled("new-fruit-feature", "variant-1")) {
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
