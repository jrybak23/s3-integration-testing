package com.example.controller;

import com.example.controller.testconfig.S3Resource;
import com.example.dto.FormData;
import com.example.repository.S3Repository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Stream;

import static com.example.controller.testutil.JSONMatchers.matchesJSON;
import static com.example.controller.testutil.TestUtil.getClasspathFile;
import static io.restassured.RestAssured.given;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTestResource(value = S3Resource.class)
@QuarkusTest
class FileControllerTest {

    @Inject
    S3Repository s3Repository;

    @BeforeEach
    public void beforeEach() {
        s3Repository.createBucketIfNotExists();
    }

    @AfterEach
    void afterEach() {
        s3Repository.deleteAllObjects();
    }

    /**
     * test for {@link FileController#listFiles()}
     */
    @Test
    void testListFiles() {
        s3Repository.putObject("1.jpg", getClasspathFile("test/files/photo1.jpg"));
        s3Repository.putObject("2.jpg", getClasspathFile("test/files/photo2.jpg"));

        given()
                .when().get("/files")
                .then()
                .statusCode(200)
                .body(matchesJSON("[\n\"1.jpg\",\n\"2.jpg\"\n]"));
    }

    /**
     * test for {@link FileController#uploadFile(FormData)}
     */
    @ParameterizedTest
    @MethodSource("listTestFiles")
    void testUploadFile(File file) throws IOException {
        String contentType = Files.probeContentType(file.toPath());
        String objectKey = "test-upload-" + file.getName();
        given()
                .when()
                .formParam("filename", objectKey)
                .formParam("mimetype", contentType)
                .multiPart("file", file, contentType)
                .post("/files")
                .then()
                .statusCode(200);

        long objectSize = s3Repository.getObjectSize(objectKey);
        long expectedFileSize = Files.size(file.toPath());
        assertEquals(expectedFileSize, objectSize, "The file " + file.getName() + " is not uploaded correctly.");
    }

    public static Stream<Arguments> listTestFiles() {
        File directory = getClasspathFile("test/files");
        File[] testFiles = requireNonNull(directory.listFiles(), "Test files dir is empty.");
        return Arrays.stream(testFiles)
                .map(Arguments::arguments);
    }
}