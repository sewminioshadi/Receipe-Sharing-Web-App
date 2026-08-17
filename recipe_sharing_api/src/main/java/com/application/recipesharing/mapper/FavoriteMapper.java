package com.application.recipesharing.mapper;

import com.application.recipesharing.dto.favorite.FavoriteResponse;
import com.application.recipesharing.dto.recipe.RecipeSummaryResponse;
import com.application.recipesharing.entity.Favorite;

public final class FavoriteMapper {

    private FavoriteMapper() {
    }

    public static FavoriteResponse toResponse(Favorite favorite, RecipeSummaryResponse recipeSummary) {
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .createdAt(favorite.getCreatedAt())
                .recipe(recipeSummary)
                .build();
    }
}
