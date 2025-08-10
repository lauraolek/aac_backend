package com.augmentative.communication.controller;

import com.augmentative.communication.dto.ChildProfileDTO;
import com.augmentative.communication.service.ChildProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST Controller for ChildProfile-related operations.
 * Handles CRUD operations for child profiles.
 */
@RestController
@RequestMapping("/api/profiles")
public class ChildProfileController {

    private final ChildProfileService childProfileService;

    public ChildProfileController(ChildProfileService childProfileService) {
        this.childProfileService = childProfileService;
    }

    /**
     * Retrieves all child profiles for a specific user. Requires authentication.
     *
     * @param userId The ID of the user.
     * @return A list of child profile DTOs.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChildProfileDTO>> getChildProfilesByUserId(@PathVariable Long userId) {
        List<ChildProfileDTO> profiles = childProfileService.findByUserId(userId);
        return new ResponseEntity<>(profiles, HttpStatus.OK);
    }

    /**
     * Retrieves a specific child profile by its ID. Requires authentication.
     *
     * @param id The ID of the child profile.
     * @return The child profile DTO if found, or HTTP status 404 (Not Found).
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ChildProfileDTO> getChildProfileById(@PathVariable Long id) {
        return childProfileService.findById(id)
                .map(profileDTO -> new ResponseEntity<>(profileDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Creates a new child profile for a given user. Requires authentication.
     *
     * @param userId The ID of the user to associate the profile with.
     * @param childProfileDTO The child profile DTO to create.
     * @return The created child profile DTO with HTTP status 201 (Created).
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/user/{userId}")
    public ResponseEntity<ChildProfileDTO> createChildProfile(@PathVariable Long userId, @RequestBody ChildProfileDTO childProfileDTO) {
        try {
            ChildProfileDTO savedProfile = childProfileService.save(userId, childProfileDTO);
            return new ResponseEntity<>(savedProfile, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Or more specific error handling
        }
    }

    /**
     * Updates an existing child profile. Requires authentication.
     *
     * @param id The ID of the child profile to update.
     * @param childProfileDTO The updated child profile DTO.
     * @return The updated child profile DTO, or HTTP status 404 (Not Found).
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<ChildProfileDTO> updateChildProfile(@PathVariable Long id, @RequestBody ChildProfileDTO childProfileDTO) {
        try {
            ChildProfileDTO updatedProfile = childProfileService.update(id, childProfileDTO);
            return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a child profile by its ID. Requires authentication.
     *
     * @param id The ID of the child profile to delete.
     * @return HTTP status 204 (No Content) on successful deletion, or 404 (Not Found).
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChildProfile(@PathVariable Long id) {
        try {
            childProfileService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) { // Catching generic Exception for simplicity, but could be more specific
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}