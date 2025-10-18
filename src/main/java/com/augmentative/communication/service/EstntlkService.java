package com.augmentative.communication.service;

import com.augmentative.communication.dto.ProcessSentenceRequest;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Service for integrating with estntlk.
 * This is a mocked service as estntlk is likely a Python library.
 * In a real application, this would involve calling an external Python service
 * (e.g., via REST API, gRPC, or a message queue).
 */
@Service
public class EstntlkService {

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
}