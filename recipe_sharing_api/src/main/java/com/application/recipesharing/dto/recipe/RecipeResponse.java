package com.application.recipesharing.dto.recipe;

import java.time.LocalDateTime;
import java.util.List;

import com.application.recipesharing.enums.Difficulty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeResponse {

    private Long id;

    private String title;

    private String description;

    private String instructions;

    private Integer cookingTime;

    private Difficulty difficulty;

    private Integer servings;

    private String imageUrl;

    private String authorName;

    private String categoryName;

    private List<IngredientResponse> ingredients;

    private Double avgRating;

    private Long likeCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
