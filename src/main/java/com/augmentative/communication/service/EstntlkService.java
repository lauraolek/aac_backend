package com.augmentative.communication.service;

import com.augmentative.communication.dto.ProcessSentenceRequest;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EstntlkService {

    private static final String API_URL = "https://api.tartunlp.ai/text-to-speech/v2";
    private static final int MAX_RETRIES = 5;
    private static final long BASE_DELAY_MS = 500;
    private final HttpClient httpClient;

    /**
     * Inner record representing the TTS request payload.
     * Records are a concise way to create immutable data classes in modern Java.
     */
    private record TTSRequest(String text, String speaker, double speed) {}

    public EstntlkService() {
        // Initialize a single, reusable HttpClient instance with a timeout.
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(15))
                .build();
        System.out.println("EstntlkService initialized.");
    }

    /**
     * Mocks the processing of a sentence using estntlk.
     * In a real scenario, this would send the sentence to an estntlk service
     * and return the modified sentence.
     *
     * @param request The input request containing the sentence.
     * @return The "modified" sentence.
     */
    public String processSentence(ProcessSentenceRequest request) {
        try {
            System.out.println("Mocking estntlk processing for sentence: \"" + request.getSentence() + "\"");
            var wordList = request.getSentence().split(",");
            var sb = new StringBuilder();
            sb.append("\"[");
            for (int i = 0; i < wordList.length; i++) {
                String word = wordList[i].trim();

                sb.append("'").append(word).append("'");

                if (i < wordList.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]\"");

            // Build the command to run the script
            ProcessBuilder pb = new ProcessBuilder("python", "script.py", sb.toString());
            pb.redirectErrorStream(true);

            // Set the working directory if needed
            pb.directory(new java.io.File("src/main/resources/estNtlkScript"));

            // Start the process
            Process process = pb.start();

            // Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Python script exited with code " + exitCode);
            }

            output.delete(0, 1);
            output.delete(output.length() - 1, output.length());

            System.out.println(output);
            return output.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "error: Failed to run script.";
        }
    }


    /**
     * Sends a POST request to the TTS API, converts the given text to speech,
     * and returns the raw audio content as bytes.
     *
     * @param text The text string to be converted (e.g., "Tere!").
     * @param speaker The voice model to use (e.g., "mari", "madis").
     * @param speed The speed of speech (e.g., 1.0).
     * @return The raw audio content as a byte array (typically MP3 data).
     * @throws IOException If a non-retryable or final network/HTTP error occurs.
     * @throws InterruptedException If the thread is interrupted during sleep/wait.
     */
    public byte[] textToSpeech(String text, String speaker, double speed)
            throws IOException, InterruptedException {

        // 1. Construct the JSON payload string
        TTSRequest requestBody = new TTSRequest(text, speaker, speed);
        // Using String.format for this simple payload. Use ObjectMapper in Spring Boot.
        String jsonPayload = String.format("{\"text\":\"%s\",\"speaker\":\"%s\",\"speed\":%f}",
                requestBody.text(), requestBody.speaker(), requestBody.speed());

        // 2. Define the base request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Accept", "audio/wav")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        String logText = text.length() > 30 ? text.substring(0, 30) + "..." : text;
        System.out.printf("Requesting TTS for text: '%s' with speaker: %s%n", logText, speaker);

        // 3. Implement POST request with Exponential Backoff
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            // Calculate delay with jitter: BASE_DELAY * 2^attempt + random(0 to BASE_DELAY)
            long delay = BASE_DELAY_MS * (long) Math.pow(2, attempt)
                    + ThreadLocalRandom.current().nextLong(BASE_DELAY_MS);

            try {
                // Execute the request, expecting binary response body (byte[])
                HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
                int statusCode = response.statusCode();

                if (statusCode >= 200 && statusCode < 300) {
                    // Success!
                    return response.body();
                }

                // Handle retryable errors (429 Too Many Requests, 5xx Server Errors)
                if (statusCode == 429 || statusCode >= 500) {
                    if (attempt < MAX_RETRIES - 1) {
                        System.out.printf("Retryable error (Status %d) on attempt %d. Waiting %dms...%n",
                                statusCode, attempt + 1, delay);
                        Thread.sleep(delay);
                        continue; // Go to the next attempt
                    } else {
                        throw new IOException("Server failed after " + MAX_RETRIES + " attempts. Last Status: " + statusCode);
                    }
                }

                // Handle non-retryable client errors (4xx other than 429)
                String errorBody = new String(response.body());
                throw new IOException(String.format("Non-retryable API Client Error: Status %d. Body: %s...",
                        statusCode, errorBody.substring(0, Math.min(errorBody.length(), 100))));

            } catch (IOException e) {
                // Handle network-related errors
                if (attempt < MAX_RETRIES - 1) {
                    System.err.printf("Network error on attempt %d: %s. Waiting %dms...%n",
                            attempt + 1, e.getMessage(), delay);
                    Thread.sleep(delay);
                    continue; // Go to the next attempt
                } else {
                    throw e; // Re-throw the last error
                }
            }
        }

        // Safety net, though exception should be thrown above
        throw new IOException("Failed to get response after all retries.");
    }
}