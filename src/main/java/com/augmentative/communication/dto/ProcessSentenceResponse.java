package com.augmentative.communication.dto;

import java.util.List;

public class ProcessSentenceResponse {
    private List<ImageWordDTO> sentence;

    private String audioBase64;

    public ProcessSentenceResponse(List<ImageWordDTO> sentence, String audioBase64) {
        this.sentence = sentence;
        this.audioBase64 = audioBase64;
    }

    public List<ImageWordDTO> getSentence() {
        return sentence;
    }

    public void setSentence(List<ImageWordDTO> sentence) {
        this.sentence = sentence;
    }

    public String getAudioBase64() {
        return audioBase64;
    }

    public void setAudioBase64(String audioBase64) {
        this.audioBase64 = audioBase64;
    }
}