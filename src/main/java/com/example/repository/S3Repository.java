package com.example.repository;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface S3Repository {
    void createBucketIfNotExists();

    void uploadObject(String objectKey, File objectContent);

    void uploadObject(String objectKey, InputStream data, String mimeType);

    List<String> listObjects();

    ResponseInputStream<GetObjectResponse> downloadObject(String objectKey);

    Long getObjectSize(String objectName);

    void deleteAllObjects();
}
