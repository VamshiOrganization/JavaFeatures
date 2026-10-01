package com.vmc.java25.virtual.threads.scalable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NetworkCall {
    public String delay(int input) {
        // Step 1: Create a URI object
        URI uri = URI.create("https://httpbin.org/delay/" + input);

        // Step 2: Build the HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        // Step 3: Create HttpClient and send request
        try (HttpClient client = HttpClient.newHttpClient()) {
            long startTime = System.currentTimeMillis();

            // This is a blocking I/O call.
            // On a Virtual Thread, the JVM automatically unmounts the virtual thread here!
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            long endTime = System.currentTimeMillis();

            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Time Taken: " + (endTime - startTime) + " ms");
            System.out.println("Response Snippet:\n" + response.body().substring(0, 120) + "...");
            return response.body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
