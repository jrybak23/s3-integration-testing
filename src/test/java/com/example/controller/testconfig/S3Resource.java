package com.example.controller.testconfig;


import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Map;

public class S3Resource implements QuarkusTestResourceLifecycleManager {
    @Override
    public Map<String, String> start() {
        // TODO: start s3
        return Map.of();
    }

    @Override
    public void stop() {

    }
}
