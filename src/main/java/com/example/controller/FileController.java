package com.example.controller;

import com.example.repository.S3Repository;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
}
