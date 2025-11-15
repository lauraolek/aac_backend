package com.augmentative.communication.dto;

import com.augmentative.communication.model.ImageWord;

public class ImageWordDTO {
    private Long id;
    private String word;
    private String imageUrl;
    //private Integer orderNumber;
    private String conjugatedWord;

    public ImageWordDTO() {
    }

    public ImageWordDTO(Long id, String word, String imageUrl, Long categoryId) {
        this.id = id;
        this.word = word;
        this.imageUrl = imageUrl;
        //this.orderNumber = orderNumber;
        //this.categoryId = categoryId;
    }

    public static ImageWordDTO fromEntity(ImageWord imageWord) {
        return new ImageWordDTO(
                imageWord.getId(),
                imageWord.getWord(),
                imageWord.getImageUrl(),
                //imageWord.getOrderNumber(),
                imageWord.getCategory().getId()
        );
    }

    public ImageWord toEntity() {
        ImageWord imageWord = new ImageWord();
        imageWord.setId(this.id);
        imageWord.setWord(this.word);
        imageWord.setImageUrl(this.imageUrl);
        //imageWord.setOrderNumber(this.orderNumber);
        return imageWord;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getConjugatedWord() {
        return conjugatedWord;
    }

    public void setConjugatedWord(String conjugatedWord) {
        this.conjugatedWord = conjugatedWord;
    }
/*
    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }
*/
}
