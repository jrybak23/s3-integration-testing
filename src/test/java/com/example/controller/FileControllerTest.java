package com.example.controller;

import com.example.controller.testconfig.S3Resource;
import com.example.repository.S3Repository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static com.example.controller.testutil.JSONMatchers.matchesJSON;
import static io.restassured.RestAssured.given;

@QuarkusTestResource(value = S3Resource.class)
@QuarkusTest
class FileControllerTest {

    @Inject
    S3Repository s3Repository;

    @BeforeEach
    public void beforeAll() {
        s3Repository.createBucketIfNotExists();
    }

    /**
     * test for {@link FileController#listFiles()}
     */
    @Test
    void testListFiles() {
        given()
                .when().get("/files")
                .then()
                .statusCode(200)
                .body(matchesJSON("[]"));
    }
}