package com.augmentative.communication.dto;

import java.util.List;

public class ProcessSentenceRequest {
    private List<ImageWordDTO> sentence;

    public List<ImageWordDTO> getSentence() {
        return sentence;
    }

    public void setSentence(List<ImageWordDTO> sentence) {
        this.sentence = sentence;
    }
}