package com.example.controller;

import com.example.controller.testconfig.S3Resource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static com.example.controller.testutil.JSONMatchers.matchesJSON;
import static io.restassured.RestAssured.given;

@QuarkusTestResource(value = S3Resource.class)
@QuarkusTest
class FileControllerTest {

    @Test
    void testListFiles() {
        given()
                .when().get("/files")
                .then()
                .statusCode(200)
                .body(matchesJSON("[]"));
    }
}