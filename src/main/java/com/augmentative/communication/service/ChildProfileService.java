package com.augmentative.communication.service;

import com.augmentative.communication.dto.CategoryDTO;
import com.augmentative.communication.dto.ChildProfileDTO;
import com.augmentative.communication.model.ChildProfile;
import com.augmentative.communication.model.User;
import com.augmentative.communication.repository.ChildProfileRepository;
import com.augmentative.communication.repository.UserRepository;
import com.augmentative.communication.util.InMemoryMultipartFile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing ChildProfile entities.
 * Handles business logic related to child profiles.
 */
@Service
public class ChildProfileService {

    private final ChildProfileRepository childProfileRepository;
    private final UserRepository userRepository;

    private final CategoryService categoryService;
    private final ImageWordService imageWordService;
    private final ImageStorageService imageStorageService;

    public ChildProfileService(ChildProfileRepository childProfileRepository, UserRepository userRepository, CategoryService categoryService, ImageWordService imageWordService, ImageStorageService imageStorageService) {
        this.childProfileRepository = childProfileRepository;
        this.userRepository = userRepository;
        this.categoryService = categoryService;
        this.imageWordService = imageWordService;
        this.imageStorageService = imageStorageService;
    }

    public List<ChildProfileDTO> findByUserId(Long userId) {
        return childProfileRepository.findByUserId(userId)
                .stream()
                .map(ChildProfileDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<ChildProfileDTO> findById(Long id) {
        return childProfileRepository.findById(id).map(ChildProfileDTO::fromEntity);
    }

    public ChildProfileDTO save(Long userId, ChildProfileDTO childProfileDTO) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            ChildProfile childProfile = childProfileDTO.toEntity();
            childProfile.setUser(userOptional.get());
            ChildProfile savedProfile = childProfileRepository.save(childProfile);

            seedCategoriesAndImageWords(savedProfile.getId());
            return ChildProfileDTO.fromEntity(savedProfile);
        }
        throw new RuntimeException("User not found with ID: " + userId);
    }

    public ChildProfileDTO update(Long profileId, ChildProfileDTO updatedProfileDTO) {
        return childProfileRepository.findById(profileId)
                .map(profile -> {
                    profile.setName(updatedProfileDTO.getName());
                    ChildProfile savedProfile = childProfileRepository.save(profile);
                    return ChildProfileDTO.fromEntity(savedProfile);
                })
                .orElseThrow(() -> new RuntimeException("ChildProfile not found with ID: " + profileId));
    }

    public void deleteById(Long id) {
        Optional<ChildProfile> childProfileOptional = childProfileRepository.findById(id);
        if (childProfileOptional.isPresent()) {
            ChildProfile childProfile = childProfileOptional.get();

            childProfile.getCategories().forEach(x -> {
                imageStorageService.deleteImage(x.getImageUrl());
                x.getImageWords().forEach(y -> imageStorageService.deleteImage(y.getImageUrl()));
            });
        }
        childProfileRepository.deleteById(id);
    }

    public List<CategoryDTO> seedCategoriesAndImageWords(Long childProfileId) {
        try {
            Path seedImagesPath = Paths.get("src/main/resources/seed-images");

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
                    childProfileId,
                    "Algused",
                    beginningsCategoryImage
            );
            CategoryDTO activitiesCategory = categoryService.save(
                    childProfileId,
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
            return new LinkedList<CategoryDTO>(Arrays.asList(beginningsCategory, activitiesCategory));
        } catch (IOException e) {
            System.err.println("Error seeding default images during initial profile setup: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Error seeding initial child profile or categories/image-words: " + e.getMessage());
        }
        return new LinkedList<CategoryDTO>();
    }
}