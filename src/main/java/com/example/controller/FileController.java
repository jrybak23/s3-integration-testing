package com.example.controller;

import com.example.dto.FormData;
import com.example.repository.S3Repository;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.util.List;

@Path("/files")
public class FileController {

    @Inject
    S3Repository s3Repository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listFiles() {
        List<String> files = s3Repository.listObjects();
        return Response.ok(files)
                .build();
    }

    @GET
    @Path("/{objectKey}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("objectKey") String objectKey) {
        ResponseInputStream<GetObjectResponse> inputStream = s3Repository.downloadObject(objectKey);
        GetObjectResponse response = inputStream.response();
        StreamingOutput responseBody = inputStream::transferTo;
        return Response.ok(responseBody, response.contentType())
                .build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@MultipartForm FormData formData) {
        s3Repository.uploadObject(formData.fileName, formData.data, formData.mimeType);
        return Response.ok("File uploaded.")
                .build();
    }
}
