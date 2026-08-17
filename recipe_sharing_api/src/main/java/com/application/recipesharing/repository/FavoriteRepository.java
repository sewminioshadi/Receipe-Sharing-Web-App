package com.application.recipesharing.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.application.recipesharing.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);
    void deleteByUserIdAndRecipeId(Long userId, Long recipeId);
}
