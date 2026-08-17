package com.application.recipesharing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.recipesharing.dto.rating.RatingRequest;
import com.application.recipesharing.dto.rating.RatingResponse;
import com.application.recipesharing.dto.rating.RatingSummaryResponse;
import com.application.recipesharing.service.RatingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recipes/{recipeId}/rating")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping
    public RatingSummaryResponse getAverageRating(@PathVariable Long recipeId) {
        return ratingService.getAverageRating(recipeId);
    }

    @PutMapping
    public RatingResponse rateRecipe(@PathVariable Long recipeId,
                                     @Valid @RequestBody RatingRequest request,
                                     @RequestHeader("X-User-Id") Long userId) {
        return ratingService.rateRecipe(recipeId, request, userId);
    }

    @GetMapping("/me")
    public RatingResponse getUserRating(@PathVariable Long recipeId,
                                        @RequestHeader("X-User-Id") Long userId) {
        return ratingService.getUserRating(recipeId, userId);
    }
}
