package com.example.repository;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class S3RepositoryImpl implements S3Repository {
    @Override
    public List<String> listObjects() {
        // TODO: implement
        return Collections.emptyList();
    }
}
