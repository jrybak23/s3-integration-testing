package com.example.repository;

import com.example.dto.DownloadObjectResponse;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface S3Repository {
    void createBucketIfNotExists();

    void uploadObject(String objectKey, File objectContent);

    void uploadObject(String objectKey, InputStream data, String mimeType);

    List<String> listObjects();

    Optional<DownloadObjectResponse> downloadObject(String objectKey);

    Long getObjectSize(String objectName);

    void deleteAllObjects();
}
