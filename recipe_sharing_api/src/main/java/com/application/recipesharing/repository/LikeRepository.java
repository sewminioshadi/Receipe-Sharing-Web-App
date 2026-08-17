package com.application.recipesharing.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.application.recipesharing.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByRecipeId(Long recipeId);
    long countByRecipeId(Long recipeId);
    boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);
    void deleteByUserIdAndRecipeId(Long userId, Long recipeId);
}
