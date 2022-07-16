package io.hreem.toggla.quarkus.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class QuarkusTogglerResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/quarkus-toggla")
                .then()
                .statusCode(200)
                .body(is("true"));
    }
}
