package com.example.repository;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
@Slf4j
public class S3RepositoryImpl implements S3Repository {

    private static final String BUCKET_NAME = "s3-integration-testing";
    private static final int MULTIPART_CHUNK_SIZE = 5242880; // 5MB (min possible multipart size for s3)

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
    public void putObject(String objectKey, File objectContent) {
        PutObjectRequest request = PutObjectRequest.builder()
                .key(objectKey)
                .bucket(BUCKET_NAME)
                .build();
        s3Client.putObject(request, RequestBody.fromFile(objectContent));
    }

    @Override
    public void putObject(String objectKey, InputStream data, String mimeType) {
        CreateMultipartUploadRequest multipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(BUCKET_NAME)
                .key(objectKey)
                .build();

        CreateMultipartUploadResponse multipartUpload = s3Client.createMultipartUpload(multipartUploadRequest);
        String uploadId = multipartUpload.uploadId();

        Collection<CompletedPart> completedParts = new ArrayList<>();
        byte[] buffer = new byte[MULTIPART_CHUNK_SIZE];
        int partNumber = 1;
        int bytesRead;
        try {
            while ((bytesRead = data.readNBytes(buffer, 0, MULTIPART_CHUNK_SIZE)) > 0) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer, 0, bytesRead);
                UploadPartRequest build = UploadPartRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(objectKey)
                        .uploadId(uploadId)
                        .partNumber(partNumber)
                        .build();

                String etag = s3Client.uploadPart(build, RequestBody.fromInputStream(inputStream, bytesRead)).eTag();
                log.info("Uploaded part etag: " + etag + ", bytes read: "+ bytesRead);
                CompletedPart part = CompletedPart.builder().partNumber(partNumber).eTag(etag).build();
                partNumber++;
                completedParts.add(part);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(completedParts)
                .build();

        CompleteMultipartUploadRequest build = CompleteMultipartUploadRequest.builder()
                .bucket(BUCKET_NAME)
                .key(objectKey)
                .uploadId(uploadId)
                .multipartUpload(completedMultipartUpload)
                .build();
        s3Client.completeMultipartUpload(build);
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
    public Long getObjectSize(String objectKey) {
        HeadObjectRequest request = HeadObjectRequest.builder()
                .key(objectKey)
                .bucket(BUCKET_NAME)
                .build();
        HeadObjectResponse response = s3Client.headObject(request);
        return response.contentLength();
    }
}
