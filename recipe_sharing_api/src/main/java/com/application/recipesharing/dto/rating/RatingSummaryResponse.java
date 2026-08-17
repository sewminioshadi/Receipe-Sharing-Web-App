package com.application.recipesharing.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingSummaryResponse {

    private Long recipeId;

    private Double averageRating;

    private Long ratingCount;
}
