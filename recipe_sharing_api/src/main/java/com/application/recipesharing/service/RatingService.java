package com.application.recipesharing.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.recipesharing.dto.rating.RatingRequest;
import com.application.recipesharing.dto.rating.RatingResponse;
import com.application.recipesharing.dto.rating.RatingSummaryResponse;
import com.application.recipesharing.entity.Rating;
import com.application.recipesharing.entity.Recipe;
import com.application.recipesharing.entity.User;
import com.application.recipesharing.exception.ResourceNotFoundException;
import com.application.recipesharing.mapper.RatingMapper;
import com.application.recipesharing.repository.RatingRepository;
import com.application.recipesharing.repository.RecipeRepository;
import com.application.recipesharing.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Transactional
    public RatingResponse rateRecipe(Long recipeId, RatingRequest request, Long userId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Optional<Rating> existingRating = ratingRepository.findByRecipeIdAndUserId(recipeId, userId);

        if (existingRating.isPresent()) {
            Rating rating = existingRating.get();
            rating.setScore(request.getScore());
            return RatingMapper.toResponse(ratingRepository.save(rating));
        }

        Rating rating = Rating.builder()
                .score(request.getScore())
                .recipe(recipe)
                .user(user)
                .build();
        return RatingMapper.toResponse(ratingRepository.save(rating));
    }

    @Transactional(readOnly = true)
    public RatingSummaryResponse getAverageRating(Long recipeId) {
        return RatingMapper.toSummaryResponse(
                recipeId,
                ratingRepository.getAverageScoreByRecipeId(recipeId),
                ratingRepository.countByRecipeId(recipeId));
    }

    @Transactional(readOnly = true)
    public RatingResponse getUserRating(Long recipeId, Long userId) {
        return ratingRepository.findByRecipeIdAndUserId(recipeId, userId)
                .map(RatingMapper::toResponse)
                .orElse(null);
    }
}
