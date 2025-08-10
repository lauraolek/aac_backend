package com.augmentative.communication.service;

import com.augmentative.communication.model.ChildProfile;
import com.augmentative.communication.model.User;
import com.augmentative.communication.repository.ChildProfileRepository;
import com.augmentative.communication.repository.UserRepository;
import com.augmentative.communication.dto.ChildProfileDTO;
import org.springframework.stereotype.Service;
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

    public ChildProfileService(ChildProfileRepository childProfileRepository, UserRepository userRepository) {
        this.childProfileRepository = childProfileRepository;
        this.userRepository = userRepository;
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
        childProfileRepository.deleteById(id);
    }
}