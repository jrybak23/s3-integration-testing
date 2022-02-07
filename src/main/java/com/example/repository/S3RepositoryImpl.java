package com.example.repository;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
@Slf4j
public class S3RepositoryImpl implements S3Repository {

    public static final String BUCKET_NAME = "s3-integration-testing";

    @Inject
    S3Client s3Client;

    @Inject
    S3Uploader s3Uploader;

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
    public void uploadObject(String objectKey, File objectContent) {
        PutObjectRequest request = PutObjectRequest.builder()
                .key(objectKey)
                .bucket(BUCKET_NAME)
                .build();
        s3Client.putObject(request, RequestBody.fromFile(objectContent));
    }

    @Override
    public void uploadObject(String objectKey, InputStream data, String mimeType) {
        s3Uploader.uploadObject(objectKey, data, mimeType);
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

    @Override
    public ResponseInputStream<GetObjectResponse> downloadObject(String objectKey) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(objectKey)
                .build();
        return s3Client.getObject(request);
    }

    @Override
    public Long getObjectSize(String objectKey) {
        HeadObjectRequest request = HeadObjectRequest.builder()
                .key(objectKey)
                .bucket(BUCKET_NAME)
                .build();
        HeadObjectResponse response = s3Client.headObject(request);
        return response.contentLength();
    }

    @Override
    public void deleteAllObjects() {
        List<String> allObjectKeys = listObjects();
        if (!allObjectKeys.isEmpty()) {
            DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                    .bucket(BUCKET_NAME)
                    .delete(createDeleteObjectsOperation(allObjectKeys))
                    .build();
            s3Client.deleteObjects(request);
        }
    }

    private Delete createDeleteObjectsOperation(List<String> objectKeys) {
        List<ObjectIdentifier> objects = objectKeys.stream()
                .map(this::createObjectIdentifier)
                .collect(toList());
        return Delete.builder()
                .objects(objects)
                .build();
    }

    private ObjectIdentifier createObjectIdentifier(String objectKey) {
        return ObjectIdentifier.builder()
                .key(objectKey)
                .build();
    }
}
