package com.application.recipesharing.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.application.recipesharing.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameIgnoreCase(String name);
    boolean existsByName(String name);
}
