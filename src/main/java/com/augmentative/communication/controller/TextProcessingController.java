package com.augmentative.communication.controller;

import com.augmentative.communication.dto.ImageWordDTO;
import com.augmentative.communication.dto.ProcessAudioRequest;
import com.augmentative.communication.dto.ProcessSentenceRequest;
import com.augmentative.communication.dto.ProcessSentenceResponse;
import com.augmentative.communication.service.EstntlkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

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

        processedSentence = processedSentence.replace("'", "");
        var newWords = new ArrayList<String>(Arrays.asList(processedSentence.split(",")));
        System.out.println(newWords);

        for (int i = 0; i < newWords.size(); i++) {
            String cleanWord = newWords.get(i).strip();
            request.getSentence().get(i).setConjugatedWord(cleanWord);
        }
        try {
            var audioBytes = estntlkService.textToSpeech(processedSentence, "mari", 1);
            String audioBase64 = Base64.getEncoder().encodeToString(audioBytes);

            return new ProcessSentenceResponse(
                    request.getSentence(),
                    audioBase64
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/conjugate")
    public List<ImageWordDTO> conjugateSentence(@RequestBody ProcessSentenceRequest request) {
        String processedSentence = estntlkService.processSentence(request);

        processedSentence = processedSentence.replace("'", "");
        var newWords = new ArrayList<String>(Arrays.asList(processedSentence.split(",")));
        System.out.println(newWords);

        for (int i = 0; i < newWords.size(); i++) {
            String cleanWord = newWords.get(i).strip();
            request.getSentence().get(i).setConjugatedWord(cleanWord);
        }
        return request.getSentence();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/audio")
    public ResponseEntity<String> processAudio(@RequestBody ProcessAudioRequest request) {

        try {
            var audioBytes = estntlkService.textToSpeech(String.join(", ", request.getSentence()), "mari", 1);
            String audioBase64 = Base64.getEncoder().encodeToString(audioBytes);

            return new ResponseEntity<>(audioBase64, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}