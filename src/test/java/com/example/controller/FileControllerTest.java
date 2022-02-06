package com.example.controller;

import com.example.controller.testconfig.S3Resource;
import com.example.controller.testutil.TestUtil;
import com.example.repository.S3Repository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.io.File;
import java.io.InputStream;

import static com.example.controller.testutil.JSONMatchers.matchesJSON;
import static com.example.controller.testutil.TestUtil.getClasspathFile;
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
        s3Repository.putObject("1.jpg", getClasspathFile("images/photo1.jpg"));
        s3Repository.putObject("2.jpg", getClasspathFile("images/photo2.jpg"));

        given()
                .when().get("/files")
                .then()
                .statusCode(200)
                .body(matchesJSON("[\n\"1.jpg\",\n\"2.jpg\"\n]"));
    }
}