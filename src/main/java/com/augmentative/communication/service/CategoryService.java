package com.augmentative.communication.service;

import com.augmentative.communication.model.Category;
import com.augmentative.communication.model.ChildProfile;
import com.augmentative.communication.repository.CategoryRepository;
import com.augmentative.communication.repository.ChildProfileRepository;
import com.augmentative.communication.dto.CategoryDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing Category entities.
 * Handles business logic related to categories.
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ChildProfileRepository childProfileRepository;
    private final ImageStorageService imageStorageService;

    public CategoryService(CategoryRepository categoryRepository, ChildProfileRepository childProfileRepository, ImageStorageService imageStorageService) {
        this.categoryRepository = categoryRepository;
        this.childProfileRepository = childProfileRepository;
        this.imageStorageService = imageStorageService;
    }

    @PreAuthorize("isAuthenticated()")
    public List<CategoryDTO> findByChildProfileId(Long childProfileId) {
        return categoryRepository.findByChildProfileIdOrderByOrderNumberAsc(childProfileId)
                .stream()
                .map(CategoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PreAuthorize("isAuthenticated()")
    public Optional<CategoryDTO> findById(Long id) {
        return categoryRepository.findById(id).map(CategoryDTO::fromEntity);
    }

    @PreAuthorize("isAuthenticated()")
    public CategoryDTO save(Long childProfileId, String name, MultipartFile imageFile) throws IOException {
        Optional<ChildProfile> childProfileOptional = childProfileRepository.findById(childProfileId);
        if (childProfileOptional.isPresent()) {
            // Save the image and get the URL
            String imageUrl = imageStorageService.saveImage(imageFile);
            System.out.println(imageUrl);
            Category category = new Category();
            category.setChildProfile(childProfileOptional.get());
            category.setName(name);
            //category.setOrderNumber(orderNumber);
            category.setImageUrl(imageUrl); // Set the URL from the storage service
            Category savedCategory = categoryRepository.save(category);
            return CategoryDTO.fromEntity(savedCategory);
        }
        throw new RuntimeException("ChildProfile not found with ID: " + childProfileId);
    }

    @PreAuthorize("isAuthenticated()")
    public CategoryDTO update(Long categoryId, String name, MultipartFile imageFile) throws IOException {
        return categoryRepository.findById(categoryId)
                .map(category -> {
                    category.setName(name);
                    //category.setOrderNumber(orderNumber);

                    // If a new image is provided, upload it and update the URL
                    var oldImageUrl = category.getImageUrl();
                    var hasNewImage = imageFile != null && !imageFile.isEmpty();
                    if (hasNewImage) {
                        try {
                            String newImageUrl = imageStorageService.saveImage(imageFile);
                            category.setImageUrl(newImageUrl);
                        } catch (IOException e) {
                            hasNewImage = false;
                            throw new RuntimeException("Failed to upload new image.", e);
                        }
                    }

                    Category savedCategory = categoryRepository.save(category);
                    if (hasNewImage) {
                        imageStorageService.deleteImage(oldImageUrl);
                    }
                    return CategoryDTO.fromEntity(savedCategory);
                })
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
    }

    @PreAuthorize("isAuthenticated()")
    public void deleteById(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();

            category.getImageWords().forEach(x -> imageStorageService.deleteImage(x.getImageUrl()));
            imageStorageService.deleteImage(category.getImageUrl());

            categoryRepository.deleteById(id);
        } else {
            throw new RuntimeException("Category not found with ID: " + id);
        }
    }
}