package com.application.recipesharing.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.application.recipesharing.entity.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByRecipeId(Long recipeId);
    void deleteByRecipeId(Long recipeId);
}
