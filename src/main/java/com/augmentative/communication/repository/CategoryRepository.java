package com.augmentative.communication.repository;

import com.augmentative.communication.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository for Category entities.
 * Provides methods for CRUD operations and finding categories by child profile.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByChildProfileIdOrderByOrderNumberAsc(Long childProfileId);
}