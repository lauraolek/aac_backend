package com.augmentative.communication.service;

import com.augmentative.communication.model.User;
import com.augmentative.communication.repository.UserRepository;
import com.augmentative.communication.dto.UserDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * Service class for managing User entities.
 * Handles business logic related to users, such as registration and retrieval.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
}