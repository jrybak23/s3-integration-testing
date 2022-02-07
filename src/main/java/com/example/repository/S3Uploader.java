package com.example.repository;

import java.io.InputStream;

public interface S3Uploader {
    void uploadObject(String objectKey, InputStream data, String mimeType);
}
