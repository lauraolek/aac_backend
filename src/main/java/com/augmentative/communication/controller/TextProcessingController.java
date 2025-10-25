package com.augmentative.communication.controller;

import com.augmentative.communication.dto.ProcessSentenceRequest;
import com.augmentative.communication.dto.ProcessSentenceResponse;
import com.augmentative.communication.service.EstntlkService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Base64;

/**
 * REST Controller for text processing operations, specifically for estntlk integration.
 */
@RestController
@RequestMapping("/api/text")
public class TextProcessingController {

    private final EstntlkService estntlkService;

    public TextProcessingController(EstntlkService estntlkService) {
        this.estntlkService = estntlkService;
    }

    /**
     * Endpoint to process a sentence using the estntlk service.
     * This is currently mocked. Requires authentication.
     *
     * @param request A string of comma-separated words
     * @return A response containing the processed sentence.
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/process")
    public ProcessSentenceResponse processSentence(@RequestBody ProcessSentenceRequest request) {
        String processedSentence = estntlkService.processSentence(request);
        try {
            var audioBytes = estntlkService.textToSpeech(processedSentence, "mari", 1);
            String audioBase64 = Base64.getEncoder().encodeToString(audioBytes);

            return new ProcessSentenceResponse(
                    processedSentence,
                    audioBase64
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}