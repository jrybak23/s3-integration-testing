# AWS S3 integration testing

Example of S3 testing setup using Java, LocalStack and Testcontainers.

**Read [the related article https://www.devguidance.com/posts/s3-java-integration-test/](https://www.devguidance.com/posts/s3-java-integration-test/)**

Click to navigate to:
- [The testing class](https://github.com/jrybak23/s3-integration-testing/blob/master/src/test/java/com/example/controller/FileControllerTest.java)
- [The controller class](https://github.com/jrybak23/s3-integration-testing/blob/master/src/main/java/com/example/controller/FileController.java)
- [S3 repository implementation](https://github.com/jrybak23/s3-integration-testing/blob/master/src/main/java/com/example/repository/S3RepositoryImpl.java)
- [S3 multipart upload implementation](https://github.com/jrybak23/s3-integration-testing/blob/master/src/main/java/com/example/repository/S3MultipartUploader.java)
- [S3 test provisioning](https://github.com/jrybak23/s3-integration-testing/blob/master/src/test/java/com/example/controller/testconfig/S3Resource.java)

---------------------------------------------------------------------------------
This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory. Be aware that it’s not an _über-jar_ as
the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/s3-integration-testing-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- RESTEasy JAX-RS ([guide](https://quarkus.io/guides/rest-json)): REST endpoint framework implementing JAX-RS and more

## Provided Code

### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources)
