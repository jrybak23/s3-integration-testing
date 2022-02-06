package com.example.repository;

import java.io.File;
import java.util.List;

public interface S3Repository {
    void createBucketIfNotExists();

    void putObject(String objectKey, File objectContent);

    List<String> listObjects();
}
