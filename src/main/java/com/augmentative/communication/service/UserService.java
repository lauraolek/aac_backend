package com.augmentative.communication.service;

import com.augmentative.communication.dto.CategoryDTO;
import com.augmentative.communication.dto.ChildProfileDTO;
import com.augmentative.communication.dto.UserDTO;
import com.augmentative.communication.model.User;
import com.augmentative.communication.repository.UserRepository;
import com.augmentative.communication.util.InMemoryMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing User entities.
 * Handles business logic related to users, such as registration and retrieval.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ChildProfileService childProfileService;
    private final CategoryService categoryService;
    private final ImageWordService imageWordService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ChildProfileService
            childProfileService, CategoryService categoryService, ImageWordService imageWordService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.childProfileService = childProfileService;
        this.categoryService = categoryService;
        this.imageWordService = imageWordService;
    }

    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id).map(UserDTO::fromEntity);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username); // Return entity for security context
    }

    public UserDTO save(UserDTO userDTO) {
        User user = userDTO.toEntity();
        // Hash the password before saving
        user.setPasswordHash(passwordEncoder.encode(userDTO.getPasswordHash()));
        User savedUser = userRepository.save(user);

        return UserDTO.fromEntity(savedUser);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public void seedInitialChildProfile(Long userId) {
        List<ChildProfileDTO> existingProfiles = childProfileService.findByUserId(userId);

        if (existingProfiles.isEmpty()) {
            System.out.println("User " + userId + " has no profiles. Seeding initial data.");
            
            Path seedImagesPath = Paths.get("src/main/resources/seed-images");

            try {
                ChildProfileDTO newChildProfileDto = new ChildProfileDTO();
                newChildProfileDto.setName("Vaikimisi");
                newChildProfileDto.setUserId(userId);
                ChildProfileDTO savedChildProfile = childProfileService.save(userId, newChildProfileDto);
                System.out.println("Seeded child profile: " + savedChildProfile.getName());


                byte[] beginningsCategoryImageBytes = Files.readAllBytes(seedImagesPath.resolve("beginning.png"));
                InMemoryMultipartFile beginningsCategoryImage = new InMemoryMultipartFile(beginningsCategoryImageBytes, "beginning.png", "image/png");

                byte[] activitiesCategoryImageBytes = Files.readAllBytes(seedImagesPath.resolve("activity.png"));
                InMemoryMultipartFile activitiesCategoryImage = new InMemoryMultipartFile(activitiesCategoryImageBytes, "activity.png", "image/png");

                byte[] iWantImageBytes = Files.readAllBytes(seedImagesPath.resolve("I want.png"));
                InMemoryMultipartFile iWantImage = new InMemoryMultipartFile(iWantImageBytes, "I want.png", "image/png");
                byte[] iSeeImageBytes = Files.readAllBytes(seedImagesPath.resolve("see.png"));
                InMemoryMultipartFile iSeeImage = new InMemoryMultipartFile(iSeeImageBytes, "see.png", "image/png");

                byte[] playImageBytes = Files.readAllBytes(seedImagesPath.resolve("play.png"));
                InMemoryMultipartFile playImage = new InMemoryMultipartFile(playImageBytes, "play.png", "image/png");
                byte[] sleepImageBytes = Files.readAllBytes(seedImagesPath.resolve("sleep.png"));
                InMemoryMultipartFile sleepImage = new InMemoryMultipartFile(sleepImageBytes, "sleep.png", "image/png");
                byte[] eatImageBytes = Files.readAllBytes(seedImagesPath.resolve("eat.png"));
                InMemoryMultipartFile eatImage = new InMemoryMultipartFile(eatImageBytes, "eat.png", "image/png");


                CategoryDTO beginningsCategory = categoryService.save(
                        savedChildProfile.getId(),
                        "Algused",
                        beginningsCategoryImage
                );
                CategoryDTO activitiesCategory = categoryService.save(
                        savedChildProfile.getId(),
                        "Tegevused",
                        activitiesCategoryImage
                );
                System.out.println("Seeded categories.");


                imageWordService.save(beginningsCategory.getId(), "Ma tahan", iWantImage);
                imageWordService.save(beginningsCategory.getId(), "Ma näen", iSeeImage);

                imageWordService.save(activitiesCategory.getId(), "mängima", playImage);
                imageWordService.save(activitiesCategory.getId(), "sööma", eatImage);
                imageWordService.save(activitiesCategory.getId(), "magama", sleepImage);
                System.out.println("Seeded image words.");

            } catch (IOException e) {
                System.err.println("Error seeding default images during initial profile setup: " + e.getMessage());
            } catch (RuntimeException e) {
                System.err.println("Error seeding initial child profile or categories/image-words: " + e.getMessage());
            }
        }
    }
}