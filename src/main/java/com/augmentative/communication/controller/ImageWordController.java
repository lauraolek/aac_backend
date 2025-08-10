package com.augmentative.communication.controller;

import com.augmentative.communication.dto.ImageWordDTO;
import com.augmentative.communication.service.ImageWordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * REST Controller for ImageWord-related operations.
 * Handles CRUD operations for image+words within categories.
 */
@RestController
@RequestMapping("/api/imagewords")
public class ImageWordController {

    private final ImageWordService imageWordService;

    public ImageWordController(ImageWordService imageWordService) {
        this.imageWordService = imageWordService;
    }

    /**
     * Retrieves all image+words for a specific category, ordered by orderNumber. Requires authentication.
     *
     * @param categoryId The ID of the category.
     * @return A list of image+word DTOs.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ImageWordDTO>> getImageWordsByCategoryId(@PathVariable Long categoryId) {
        List<ImageWordDTO> imageWords = imageWordService.findByCategoryId(categoryId);
        return new ResponseEntity<>(imageWords, HttpStatus.OK);
    }

    /**
     * Retrieves a specific image+word by its ID. Requires authentication.
     *
     * @param id The ID of the image+word.
     * @return The image+word DTO if found, or HTTP status 404 (Not Found).
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ImageWordDTO> getImageWordById(@PathVariable Long id) {
        return imageWordService.findById(id)
                .map(imageWordDTO -> new ResponseEntity<>(imageWordDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Creates a new image+word for a given category with a file upload. Requires authentication.
     *
     * @param categoryId The ID of the category to associate the image+word with.
     * @param wordText The word associated with the image.
     * @param imageFile The image file for the image+word.
     * @return The created image+word DTO with HTTP status 201 (Created).
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/category/{categoryId}", consumes = {"multipart/form-data"})
    public ResponseEntity<ImageWordDTO> createImageWord(
            @PathVariable Long categoryId,
            @RequestParam("word") String wordText,
            //@RequestParam("orderNumber") Integer orderNumber,
            @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            ImageWordDTO savedImageWord = imageWordService.save(categoryId, wordText, imageFile);
            return new ResponseEntity<>(savedImageWord, HttpStatus.CREATED);
        } catch (RuntimeException | IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Updates an existing image+word, optionally with a new image. Requires authentication.
     *
     * @param id The ID of the image+word to update.
     * @param wordText The new word for the image+word.
     * @param orderNumber The new order number for the image+word.
     * @param imageFile An optional new image file for the image+word.
     * @return The updated image+word DTO, or HTTP status 404 (Not Found).
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ImageWordDTO> updateImageWord(
            @PathVariable Long id,
            @RequestParam("wordText") String wordText,
            @RequestParam("orderNumber") Integer orderNumber,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            ImageWordDTO updatedImageWord = imageWordService.update(id, wordText, orderNumber, imageFile);
            return new ResponseEntity<>(updatedImageWord, HttpStatus.OK);
        } catch (RuntimeException | IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes an image+word by its ID. Requires authentication.
     *
     * @param id The ID of the image+word to delete.
     * @return HTTP status 204 (No Content) on successful deletion, or 404 (Not Found).
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImageWord(@PathVariable Long id) {
        try {
            imageWordService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}