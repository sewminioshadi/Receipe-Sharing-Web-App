package com.application.recipesharing.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.recipesharing.entity.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRecipeId(Long recipeId);
    Optional<Rating> findByRecipeIdAndUserId(Long recipeId, Long userId);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.recipe.id = :recipeId")
    Double getAverageScoreByRecipeId(@Param("recipeId") Long recipeId);

    long countByRecipeId(Long recipeId);
}
