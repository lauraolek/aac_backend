package com.augmentative.communication.dto;

import com.augmentative.communication.model.ChildProfile;

import java.util.List;
import java.util.stream.Collectors;

public class ChildProfileDTO {
    private Long id;
    private String name;
    private Long userId;

    private List<CategoryDTO> categories;

    public ChildProfileDTO() {
    }

    public ChildProfileDTO(Long id, String name, Long userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }

    public static ChildProfileDTO fromEntity(ChildProfile childProfile) {
        ChildProfileDTO dto = new ChildProfileDTO(childProfile.getId(), childProfile.getName(), childProfile.getUser().getId());
        if (childProfile.getCategories() != null) {
            dto.setCategories(childProfile.getCategories().stream()
                    .map(CategoryDTO::fromEntity) // Recursively map categories to DTOs (which now include ImageWords)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public ChildProfile toEntity() {
        ChildProfile childProfile = new ChildProfile();
        childProfile.setId(this.id);
        childProfile.setName(this.name);
        return childProfile;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
    }
}