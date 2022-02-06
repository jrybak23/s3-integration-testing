package com.example.repository;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
@Slf4j
public class S3RepositoryImpl implements S3Repository {

    private static final String BUCKET_NAME = "s3-integration-testing";

    @Inject
    S3Client s3Client;

    @Override
    public void createBucketIfNotExists() {
        CreateBucketRequest request = CreateBucketRequest.builder()
                .bucket(BUCKET_NAME)
                .build();
        try {
            s3Client.createBucket(request);
        } catch (BucketAlreadyExistsException e) {
          log.info("The bucket " + BUCKET_NAME + " was n't created as it's already exists.");
        }
    }

    @Override
    public List<String> listObjects() {
        ListObjectsRequest request = ListObjectsRequest.builder()
                .bucket(BUCKET_NAME)
                .build();

        ListObjectsResponse response = s3Client.listObjects(request);
        return response.contents().stream()
                .map(S3Object::key)
                .collect(toList());
    }
}
