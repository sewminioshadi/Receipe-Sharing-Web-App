package com.application.recipesharing.mapper;

import com.application.recipesharing.dto.rating.RatingResponse;
import com.application.recipesharing.dto.rating.RatingSummaryResponse;
import com.application.recipesharing.entity.Rating;

public final class RatingMapper {

    private RatingMapper() {
    }

    public static RatingResponse toResponse(Rating rating) {
        if (rating == null) {
            return null;
        }
        return RatingResponse.builder()
                .id(rating.getId())
                .score(rating.getScore())
                .createdAt(rating.getCreatedAt())
                .build();
    }

    public static RatingSummaryResponse toSummaryResponse(Long recipeId, Double averageRating, Long ratingCount) {
        return RatingSummaryResponse.builder()
                .recipeId(recipeId)
                .averageRating(averageRating)
                .ratingCount(ratingCount)
                .build();
    }
}
