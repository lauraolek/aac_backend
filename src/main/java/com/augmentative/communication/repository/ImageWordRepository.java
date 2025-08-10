package com.augmentative.communication.repository;

import com.augmentative.communication.model.ImageWord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository for ImageWord entities.
 * Provides methods for CRUD operations and finding image+words by category.
 */
public interface ImageWordRepository extends JpaRepository<ImageWord, Long> {
    List<ImageWord> findByCategoryIdOrderByOrderNumberAsc(Long categoryId);
}
