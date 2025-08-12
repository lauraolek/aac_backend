package com.augmentative.communication.controller;

import com.augmentative.communication.dto.CategoryDTO;
import com.augmentative.communication.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * REST Controller for Category-related operations.
 * Handles CRUD operations for categories within child profiles.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Retrieves all categories for a specific child profile, ordered by orderNumber. Requires authentication.
     *
     * @param childProfileId The ID of the child profile.
     * @return A list of category DTOs.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile/{childProfileId}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByChildProfileId(@PathVariable Long childProfileId) {
        List<CategoryDTO> categories = categoryService.findByChildProfileId(childProfileId);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    /**
     * Retrieves a specific category by its ID. Requires authentication.
     *
     * @param id The ID of the category.
     * @return The category DTO if found, or HTTP status 404 (Not Found).
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(categoryDTO -> new ResponseEntity<>(categoryDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Creates a new category for a given child profile with a file upload. Requires authentication.
     *
     * @param childProfileId The ID of the child profile to associate the category with.
     * @param name The name of the category.

     * @param imageFile The image file for the category.
     * @return The created category DTO with HTTP status 201 (Created).
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/profile/{childProfileId}", consumes = {"multipart/form-data"})
    public ResponseEntity<CategoryDTO> createCategory(
            @PathVariable Long childProfileId,
            @RequestParam("name") String name,
            //@RequestParam("orderNumber") Integer orderNumber,
            @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            System.out.println(imageFile.getName());
            System.out.println(imageFile.getSize());
            CategoryDTO savedCategory = categoryService.save(childProfileId, name, imageFile);
            return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
        } catch (RuntimeException | IOException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Updates an existing category, optionally with a new image. Requires authentication.
     *
     * @param id The ID of the category to update.
     * @param name The new name of the category.
     * @param imageFile An optional new image file for the category.
     * @return The updated category DTO, or HTTP status 404 (Not Found).
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @RequestParam("name") String name,
            //@RequestParam("orderNumber") Integer orderNumber,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            CategoryDTO updatedCategory = categoryService.update(id, name, imageFile);
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        } catch (RuntimeException | IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a category by its ID. Requires authentication.
     *
     * @param id The ID of the category to delete.
     * @return HTTP status 204 (No Content) on successful deletion, or 404 (Not Found).
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}