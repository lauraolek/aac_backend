package com.augmentative.communication.dto;

import com.augmentative.communication.model.Category;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryDTO {
    private Long id;
    private String name;
    private String imageUrl;
    //private Integer orderNumber;
    private List<ImageWordDTO> items;

    public CategoryDTO() {
    }

    public CategoryDTO(Long id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        //this.orderNumber = orderNumber;
    }

    public static CategoryDTO fromEntity(Category category) {
        CategoryDTO dto = new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getImageUrl()
                //category.getOrderNumber(),
        );
        if (category.getImageWords() != null) {
            dto.setItems(category.getImageWords().stream()
                    .map(ImageWordDTO::fromEntity)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public Category toEntity() {
        Category category = new Category();
        category.setId(this.id);
        category.setName(this.name);
        category.setImageUrl(this.imageUrl);
        //category.setOrderNumber(this.orderNumber);
        return category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

   /* public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }
*/


    public List<ImageWordDTO> getItems() {
        return items;
    }

    public void setItems(List<ImageWordDTO> imageWords) {
        this.items = imageWords;
    }
}
