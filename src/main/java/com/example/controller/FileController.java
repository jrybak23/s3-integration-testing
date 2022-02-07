package com.example.controller;

import com.example.dto.DownloadObjectResponse;
import com.example.dto.FormData;
import com.example.repository.S3Repository;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

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
        return s3Repository.downloadObject(objectKey)
                .map(this::createOkDownloadResponse)
                .orElseGet(() -> createNotFoundResponse(objectKey));
    }

    private Response createOkDownloadResponse(DownloadObjectResponse response) {
        return Response.ok(response.getStreamingOutput(), response.getContentType())
                .build();
    }

    private Response createNotFoundResponse(String objectKey) {
        return Response.status(NOT_FOUND).entity("Requested object " + objectKey + " is not found.")
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
