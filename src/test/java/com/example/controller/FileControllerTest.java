package com.example.controller;

import com.example.controller.testconfig.S3Resource;
import com.example.dto.FormData;
import com.example.repository.S3Repository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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
import static com.example.controller.testutil.TestUtil.determineContentType;
import static com.example.controller.testutil.TestUtil.getClasspathFile;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.*;
import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.is;
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
        s3Repository.uploadObject("1.jpg", getClasspathFile("test/files/photo1.jpg"));
        s3Repository.uploadObject("2.jpg", getClasspathFile("test/files/photo2.jpg"));

        given()
                .when().get("/files")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(JSON)
                .log().ifValidationFails()
                .body(matchesJSON("[\n\"1.jpg\",\n\"2.jpg\"\n]"));
    }

    /**
     * test for {@link FileController#uploadFile(FormData)}
     */
    @ParameterizedTest
    @MethodSource("listTestFiles")
    void testUploadFile(File file) throws IOException {
        String contentType = determineContentType(file);
        String objectKey = "test-upload-" + file.getName();
        given()
                .when()
                .formParam("filename", objectKey)
                .formParam("mimetype", contentType)
                .multiPart("file", file, contentType)
                .post("/files")
                .then()
                .statusCode(OK.getStatusCode())
                .log().ifValidationFails();

        long objectSize = s3Repository.getObjectSize(objectKey);
        assertEquals(getExpectedFileSize(file), objectSize, "The file " + file.getName() + " is not uploaded correctly.");
    }

    /**
     * test for {@link FileController#downloadFile(String)}
     */
    @ParameterizedTest
    @MethodSource("listTestFiles")
    void testDownloadFile(File file) throws IOException {
        String objectKey = "test-download-" + file.getName();
        s3Repository.uploadObject(objectKey, file);
        var response = given()
                .when().get("/files/" + objectKey)
                .then()
                .statusCode(OK.getStatusCode())
                .log().ifValidationFails()
                .extract();
        assertEquals(getExpectedFileSize(file), getBodyLength(response));
        ContentType expectedContentType = fromContentType(determineContentType(file));
        ContentType actualContentType = fromContentType(response.contentType());
        assertEquals(expectedContentType, actualContentType);
    }

    private int getBodyLength(ExtractableResponse<Response> response) throws IOException {
        return response.body()
                .asInputStream()
                .readAllBytes().length;
    }

    public static Stream<Arguments> listTestFiles() {
        File directory = getClasspathFile("test/files");
        File[] testFiles = requireNonNull(directory.listFiles(), "Test files dir is empty.");
        return Arrays.stream(testFiles)
                .map(Arguments::arguments);
    }

    private long getExpectedFileSize(File file) throws IOException {
        return Files.size(file.toPath());
    }

    /**
     * test for {@link FileController#downloadFile(String)}
     */
    @Test
    void testDownloadNotExistingFile() {
        String objectKey = "not-found.jpg";
        given()
                .when().get("/files/" + objectKey)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .log().ifValidationFails()
                .contentType(TEXT)
                .body(is("Requested object not-found.jpg is not found."));
    }
}