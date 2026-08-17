package com.application.recipesharing.dto.recipe;

import java.time.LocalDateTime;

import com.application.recipesharing.enums.Difficulty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeSummaryResponse {

    private Long id;

    private String title;

    private String imageUrl;

    private Integer cookingTime;

    private Difficulty difficulty;

    private Integer servings;

    private String authorName;

    private String categoryName;

    private Double avgRating;

    private Long likeCount;

    private LocalDateTime createdAt;
}
