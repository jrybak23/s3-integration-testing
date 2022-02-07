package com.example.dto;

import javax.ws.rs.core.StreamingOutput;
import java.io.InputStream;

public class DownloadObjectResponse {

    private final InputStream inputStream;
    private final String contentType;

    public DownloadObjectResponse(InputStream inputStream, String contentType) {
        this.inputStream = inputStream;
        this.contentType = contentType;
    }

    public StreamingOutput getStreamingOutput() {
        return inputStream::transferTo;
    }

    public String getContentType() {
        return contentType;
    }
}
