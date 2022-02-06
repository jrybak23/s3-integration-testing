package com.example.controller.testconfig;


import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

public class S3Resource implements QuarkusTestResourceLifecycleManager {
    static LocalStackContainer container = configureContainer();

    private static LocalStackContainer configureContainer() {
        DockerImageName localstackImage = DockerImageName.parse("localstack/localstack:0.11.3");
        return new LocalStackContainer(localstackImage)
                .withServices(S3);
    }

    @Override
    public Map<String, String> start() {
        container.start();
        String localStackUrl = container.getEndpointOverride(S3).toString();
        return Map.of(
                "quarkus.s3.endpoint-override", localStackUrl,
                "quarkus.s3.aws.credentials.static-provider.access-key-id", container.getAccessKey(),
                "quarkus.s3.aws.credentials.static-provider.secret-access-key", container.getSecretKey());
    }

    @Override
    public void stop() {
        container.stop();
    }
}
