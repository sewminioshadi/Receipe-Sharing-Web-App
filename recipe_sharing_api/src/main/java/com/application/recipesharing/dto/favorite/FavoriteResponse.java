package com.application.recipesharing.dto.favorite;

import java.time.LocalDateTime;

import com.application.recipesharing.dto.recipe.RecipeSummaryResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteResponse {

    private Long id;

    private LocalDateTime createdAt;

    private RecipeSummaryResponse recipe;
}
