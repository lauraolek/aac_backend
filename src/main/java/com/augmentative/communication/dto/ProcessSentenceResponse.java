package com.augmentative.communication.dto;

public class ProcessSentenceResponse {
    private String sentence;

    private String audioBase64;

    public ProcessSentenceResponse(String sentence, String audioBase64) {
        this.sentence = sentence;
        this.audioBase64 = audioBase64;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getAudioBase64() {
        return audioBase64;
    }

    public void setAudioBase64(String audioBase64) {
        this.audioBase64 = audioBase64;
    }
}