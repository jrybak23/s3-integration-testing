package com.example.repository;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import static com.example.repository.S3RepositoryImpl.BUCKET_NAME;

@RequestScoped
@Slf4j
public class S3MultipartUploader implements S3Uploader {


    @Inject
    S3Client s3Client;

    private static final int MULTIPART_CHUNK_SIZE = 5242880; // 5MB in bytes (min possible multipart size for s3)
    private final byte[] buffer = new byte[MULTIPART_CHUNK_SIZE];

    private String objectKey;
    private InputStream data;
    private String mimeType;


    @Override
    public void uploadObject(String objectKey, InputStream data, String mimeType) {
        this.objectKey = objectKey;
        this.data = data;
        this.mimeType = mimeType;

        String uploadId = createMultipartRequest();
        Collection<CompletedPart> completedParts = uploadAllParts(uploadId);
        completeMultipartUpload(uploadId, completedParts);
    }

    private String createMultipartRequest() {
        CreateMultipartUploadRequest multipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(BUCKET_NAME)
                .key(objectKey)
                .contentType(mimeType)
                .build();
        CreateMultipartUploadResponse multipartUpload = s3Client.createMultipartUpload(multipartUploadRequest);
        return multipartUpload.uploadId();
    }

    private Collection<CompletedPart> uploadAllParts(String uploadId) {
        Collection<CompletedPart> completedParts = new ArrayList<>();
        int partNumber = 1;
        RequestBody requestBody;
        while ((requestBody = createRequestBody()) != null) {
            UploadPartRequest uploadPartRequest = createUploadPartRequest(uploadId, partNumber);
            String etag = s3Client.uploadPart(uploadPartRequest, requestBody).eTag();
            log.info("Uploaded part etag: " + etag + ", bytes read: " + getContentLength(requestBody));
            CompletedPart part = CompletedPart.builder().partNumber(partNumber++).eTag(etag).build();
            completedParts.add(part);
        }
        return completedParts;
    }

    private RequestBody createRequestBody() {
        int bytesRead = readToBuffer(buffer);
        if (bytesRead == 0) {
            return null;
        }
        var inputStream = new ByteArrayInputStream(buffer, 0, bytesRead);
        return RequestBody.fromInputStream(inputStream, bytesRead);
    }

    private UploadPartRequest createUploadPartRequest(String uploadId, int partNumber) {
        return UploadPartRequest.builder()
                .bucket(BUCKET_NAME)
                .key(objectKey)
                .uploadId(uploadId)
                .partNumber(partNumber)
                .build();
    }

    private String getContentLength(RequestBody requestBody) {
        return requestBody.optionalContentLength().map(String::valueOf).orElse("N/A");
    }

    private int readToBuffer(byte[] buffer) {
        try {
            return data.readNBytes(buffer, 0, MULTIPART_CHUNK_SIZE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void completeMultipartUpload(String uploadId, Collection<CompletedPart> completedParts) {
        CompletedMultipartUpload multipartUpload = CompletedMultipartUpload.builder()
                .parts(completedParts)
                .build();

        CompleteMultipartUploadRequest build = CompleteMultipartUploadRequest.builder()
                .bucket(BUCKET_NAME)
                .key(objectKey)
                .uploadId(uploadId)
                .multipartUpload(multipartUpload)
                .build();
        s3Client.completeMultipartUpload(build);
    }
}
