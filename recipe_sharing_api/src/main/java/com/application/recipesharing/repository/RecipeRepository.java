package com.application.recipesharing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.recipesharing.entity.Recipe;
import com.application.recipesharing.enums.Difficulty;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByUserId(Long userId);
    List<Recipe> findByCategoryId(Long categoryId);
    List<Recipe> findByTitleContainingIgnoreCase(String title);
    List<Recipe> findByDifficulty(Difficulty difficulty);
    List<Recipe> findByCookingTimeLessThanEqual(Integer cookingTime);
    List<Recipe> findTop10ByOrderByCreatedAtDesc();
    boolean existsByUserId(Long userId);
}
