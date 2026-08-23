package com.skeleton.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class AuthResourceTest {

    @Test
    void registerThenLogin() {
        String email = "tester@example.com";

        given()
                .contentType("application/json")
                .body("{\"name\":\"Tester\",\"email\":\"" + email + "\",\"password\":\"secret123\"}")
                .when().post("/api/auth/register")
                .then().statusCode(201);

        given()
                .contentType("application/json")
                .body("{\"email\":\"" + email + "\",\"password\":\"secret123\"}")
                .when().post("/api/auth/login")
                .then().statusCode(200)
                .body("data.token", org.hamcrest.Matchers.notNullValue());
    }

    @Test
    void meWithoutTokenIsUnauthorized() {
        given()
                .when().get("/api/auth/me")
                .then().statusCode(401);
    }
}
