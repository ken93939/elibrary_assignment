package com.kenkwok.mirae.elibrary.support;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public final class S3MockServer implements AutoCloseable {

  private final Process process;
  private final URI endpoint;

  public S3MockServer() throws IOException, InterruptedException {
    int port;
    try (var socket = new ServerSocket(0)) {
      port = socket.getLocalPort();
    }
    endpoint = URI.create("http://localhost:" + port);
    Path directory = Files.createTempDirectory("elibrary-s3mock-");
    Path log = directory.resolve("server.log");
    process =
        new ProcessBuilder(
                Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-Djava.io.tmpdir=" + directory,
                "-jar",
                System.getProperty("s3mock.jar"),
                "--http.port=" + port,
                "--server.port=0",
                "--spring.main.banner-mode=off",
                "--logging.level.root=WARN")
            .redirectErrorStream(true)
            .redirectOutput(log.toFile())
            .start();
    try {
      awaitStartup(log);
    } catch (IOException | InterruptedException exception) {
      close();
      throw exception;
    }
  }

  public URI getEndpoint() {
    return endpoint;
  }

  @Override
  public void close() {
    process.destroy();
    try {
      if (!process.waitFor(5, TimeUnit.SECONDS)) {
        process.destroyForcibly();
      }
    } catch (InterruptedException exception) {
      process.destroyForcibly();
      Thread.currentThread().interrupt();
    }
  }

  private void awaitStartup(Path log) throws IOException, InterruptedException {
    long deadline = System.nanoTime() + Duration.ofSeconds(30).toNanos();
    try (var client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(1)).build()) {
      var request =
          HttpRequest.newBuilder(endpoint.resolve("/favicon.ico"))
              .timeout(Duration.ofSeconds(1))
              .GET()
              .build();
      while (process.isAlive() && System.nanoTime() < deadline) {
        try {
          if (client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode() == 200) {
            return;
          }
        } catch (IOException exception) {
          if (!process.isAlive()) {
            throw new IOException("S3Mock stopped during startup; see " + log, exception);
          }
        }
        Thread.sleep(100);
      }
    }
    throw new IOException("S3Mock did not become ready; see " + log);
  }
}
