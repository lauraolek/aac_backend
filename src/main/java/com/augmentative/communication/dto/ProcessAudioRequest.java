package com.augmentative.communication.dto;

import java.util.List;

public class ProcessAudioRequest {
    private List<String> sentence;

    public List<String> getSentence() {
        return sentence;
    }

    public void setSentence(List<String> sentence) {
        this.sentence = sentence;
    }
}