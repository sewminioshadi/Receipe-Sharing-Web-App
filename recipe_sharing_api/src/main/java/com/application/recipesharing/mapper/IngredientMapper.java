package com.application.recipesharing.mapper;

import com.application.recipesharing.dto.recipe.IngredientRequest;
import com.application.recipesharing.dto.recipe.IngredientResponse;
import com.application.recipesharing.entity.Ingredient;

public final class IngredientMapper {

    private IngredientMapper() {
    }

    public static IngredientResponse toResponse(Ingredient ingredient) {
        if (ingredient == null) {
            return null;
        }
        return IngredientResponse.builder()
                .name(ingredient.getName())
                .amount(ingredient.getAmount())
                .unit(ingredient.getUnit())
                .build();
    }

    public static Ingredient toEntity(IngredientRequest request) {
        return Ingredient.builder()
                .name(request.getName())
                .amount(request.getAmount())
                .unit(request.getUnit())
                .build();
    }
}
