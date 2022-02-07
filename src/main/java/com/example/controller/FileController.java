package com.example.controller;

import com.example.dto.FormData;
import com.example.repository.S3Repository;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/files")
public class FileController {

    @Inject
    S3Repository s3Repository;

    @GET
    public Response listFiles() {
        List<String> files = s3Repository.listObjects();
        return Response.ok(files)
                .build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@MultipartForm FormData formData) {
        s3Repository.putObject(formData.fileName, formData.data, formData.mimeType);
        return Response.ok("File uploaded.")
                .build();
    }
}
