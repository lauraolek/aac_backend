package com.augmentative.communication.repository;

import com.augmentative.communication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository for User entities.
 * Provides methods for CRUD operations and finding users by username or email.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}