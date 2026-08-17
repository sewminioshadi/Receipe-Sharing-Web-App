package com.application.recipesharing.dto.recipe;

import java.util.List;

import com.application.recipesharing.enums.Difficulty;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeUpdateRequest {

    private String title;

    private String description;

    private String instructions;

    private Integer cookingTime;

    private Difficulty difficulty;

    private Integer servings;

    private String imageUrl;

    private Long categoryId;

    @Valid
    private List<IngredientRequest> ingredients;
}
