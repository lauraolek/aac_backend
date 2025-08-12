package com.augmentative.communication.service;

import com.augmentative.communication.model.Category;
import com.augmentative.communication.model.ImageWord;
import com.augmentative.communication.repository.CategoryRepository;
import com.augmentative.communication.repository.ImageWordRepository;
import com.augmentative.communication.dto.ImageWordDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing ImageWord entities.
 * Handles business logic related to image+words.
 */
@Service
public class ImageWordService {

    private final ImageWordRepository imageWordRepository;
    private final CategoryRepository categoryRepository;
    private final ImageStorageService imageStorageService; // Inject the image storage service

    public ImageWordService(ImageWordRepository imageWordRepository, CategoryRepository categoryRepository, ImageStorageService imageStorageService) {
        this.imageWordRepository = imageWordRepository;
        this.categoryRepository = categoryRepository;
        this.imageStorageService = imageStorageService;
    }

    @PreAuthorize("isAuthenticated()")
    public List<ImageWordDTO> findByCategoryId(Long categoryId) {
        return imageWordRepository.findByCategoryIdOrderByOrderNumberAsc(categoryId)
                .stream()
                .map(ImageWordDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PreAuthorize("isAuthenticated()")
    public Optional<ImageWordDTO> findById(Long id) {
        return imageWordRepository.findById(id).map(ImageWordDTO::fromEntity);
    }

    @PreAuthorize("isAuthenticated()")
    public ImageWordDTO save(Long categoryId, String wordText, MultipartFile imageFile) throws IOException {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isPresent()) {
            // Save the image and get the URL
            String imageUrl = imageStorageService.saveImage(imageFile);

            ImageWord imageWord = new ImageWord();
            imageWord.setCategory(categoryOptional.get());
            imageWord.setWord(wordText);
           // imageWord.setOrderNumber(orderNumber);
            imageWord.setImageUrl(imageUrl); // Set the URL from the storage service
            ImageWord savedImageWord = imageWordRepository.save(imageWord);
            return ImageWordDTO.fromEntity(savedImageWord);
        }
        throw new RuntimeException("Category not found with ID: " + categoryId);
    }

    @PreAuthorize("isAuthenticated()")
    public ImageWordDTO update(Long imageWordId, String wordText, MultipartFile imageFile) throws IOException {
        return imageWordRepository.findById(imageWordId)
                .map(imageWord -> {
                    imageWord.setWord(wordText);
                    //imageWord.setOrderNumber(orderNumber);

                    // If a new image is provided, upload it and update the URL
                    if (imageFile != null && !imageFile.isEmpty()) {
                        try {
                            String newImageUrl = imageStorageService.saveImage(imageFile);
                            imageWord.setImageUrl(newImageUrl);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to upload new image.", e);
                        }
                    }

                    ImageWord savedImageWord = imageWordRepository.save(imageWord);
                    return ImageWordDTO.fromEntity(savedImageWord);
                })
                .orElseThrow(() -> new RuntimeException("ImageWord not found with ID: " + imageWordId));
    }

    @PreAuthorize("isAuthenticated()")
    public void deleteById(Long id) {
        imageWordRepository.deleteById(id);
    }
}