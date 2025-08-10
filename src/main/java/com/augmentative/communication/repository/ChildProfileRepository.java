package com.augmentative.communication.repository;

import com.augmentative.communication.model.ChildProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository for ChildProfile entities.
 * Provides methods for CRUD operations and finding child profiles by user.
 */
public interface ChildProfileRepository extends JpaRepository<ChildProfile, Long> {
    List<ChildProfile> findByUserId(Long userId);
}
