package com.application.recipesharing.mapper;

import java.util.List;

import com.application.recipesharing.dto.recipe.RecipeResponse;
import com.application.recipesharing.dto.recipe.RecipeSummaryResponse;
import com.application.recipesharing.entity.Recipe;

public final class RecipeMapper {

    private RecipeMapper() {
    }

    public static RecipeSummaryResponse toSummaryResponse(Recipe recipe, Double avgRating, Long likeCount) {
        return RecipeSummaryResponse.builder()
                .id(recipe.getId())
                .title(recipe.getTitle())
                .imageUrl(recipe.getImageUrl())
                .cookingTime(recipe.getCookingTime())
                .difficulty(recipe.getDifficulty())
                .servings(recipe.getServings())
                .authorName(recipe.getUser() != null ? recipe.getUser().getUsername() : null)
                .categoryName(recipe.getCategory() != null ? recipe.getCategory().getName() : null)
                .avgRating(avgRating)
                .likeCount(likeCount)
                .createdAt(recipe.getCreatedAt())
                .build();
    }

    public static RecipeResponse toResponse(Recipe recipe, Double avgRating, Long likeCount) {
        List<com.application.recipesharing.dto.recipe.IngredientResponse> ingredients = recipe.getIngredients() == null
                ? List.of()
                : recipe.getIngredients().stream().map(IngredientMapper::toResponse).toList();

        return RecipeResponse.builder()
                .id(recipe.getId())
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .instructions(recipe.getInstructions())
                .cookingTime(recipe.getCookingTime())
                .difficulty(recipe.getDifficulty())
                .servings(recipe.getServings())
                .imageUrl(recipe.getImageUrl())
                .authorName(recipe.getUser() != null ? recipe.getUser().getUsername() : null)
                .categoryName(recipe.getCategory() != null ? recipe.getCategory().getName() : null)
                .ingredients(ingredients)
                .avgRating(avgRating)
                .likeCount(likeCount)
                .createdAt(recipe.getCreatedAt())
                .updatedAt(recipe.getUpdatedAt())
                .build();
    }
}
