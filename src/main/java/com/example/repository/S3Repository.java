package com.example.repository;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface S3Repository {
    void createBucketIfNotExists();

    void uploadObject(String objectKey, File objectContent);

    void uploadObject(String objectKey, InputStream data, String mimeType);

    List<String> listObjects();

    Long getObjectSize(String objectName);

    void deleteAllObjects();
}
