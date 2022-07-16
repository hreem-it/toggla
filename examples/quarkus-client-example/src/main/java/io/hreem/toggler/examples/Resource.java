package io.hreem.toggla.examples;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.hreem.toggla.quarkus.TogglaClient;

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
