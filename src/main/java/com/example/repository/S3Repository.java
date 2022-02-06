package com.example.repository;

import java.util.List;

public interface S3Repository {
    void createBucketIfNotExists();

    List<String> listObjects();
}
