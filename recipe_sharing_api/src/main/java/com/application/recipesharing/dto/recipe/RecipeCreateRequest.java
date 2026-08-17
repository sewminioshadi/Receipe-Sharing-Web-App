package com.application.recipesharing.dto.recipe;

import java.util.List;

import com.application.recipesharing.enums.Difficulty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeCreateRequest {

    @NotBlank
    private String title;

    private String description;

    private String instructions;

    private Integer cookingTime;

    private Difficulty difficulty;

    private Integer servings;

    private String imageUrl;

    @NotNull
    private Long categoryId;

    @NotEmpty
    @Valid
    private List<IngredientRequest> ingredients;
}
