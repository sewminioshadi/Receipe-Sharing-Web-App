package com.application.recipesharing.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.recipesharing.dto.common.BooleanResponse;
import com.application.recipesharing.dto.favorite.FavoriteResponse;
import com.application.recipesharing.dto.recipe.RecipeSummaryResponse;
import com.application.recipesharing.entity.Favorite;
import com.application.recipesharing.entity.Recipe;
import com.application.recipesharing.entity.User;
import com.application.recipesharing.exception.ResourceNotFoundException;
import com.application.recipesharing.mapper.FavoriteMapper;
import com.application.recipesharing.mapper.RecipeMapper;
import com.application.recipesharing.repository.FavoriteRepository;
import com.application.recipesharing.repository.LikeRepository;
import com.application.recipesharing.repository.RatingRepository;
import com.application.recipesharing.repository.RecipeRepository;
import com.application.recipesharing.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public BooleanResponse toggleFavorite(Long recipeId, Long userId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (favoriteRepository.existsByUserIdAndRecipeId(userId, recipeId)) {
            favoriteRepository.deleteByUserIdAndRecipeId(userId, recipeId);
            return BooleanResponse.builder().value(false).build();
        }

        Favorite favorite = Favorite.builder()
                .recipe(recipe)
                .user(user)
                .build();
        favoriteRepository.save(favorite);
        return BooleanResponse.builder().value(true).build();
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> getFavoritesByUser(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(favorite -> FavoriteMapper.toResponse(favorite, toSummary(favorite.getRecipe())))
                .toList();
    }

    @Transactional(readOnly = true)
    public BooleanResponse isFavorited(Long recipeId, Long userId) {
        return BooleanResponse.builder()
                .value(favoriteRepository.existsByUserIdAndRecipeId(userId, recipeId))
                .build();
    }

    private RecipeSummaryResponse toSummary(Recipe recipe) {
        return RecipeMapper.toSummaryResponse(recipe, ratingRepository.getAverageScoreByRecipeId(recipe.getId()), likeRepository.countByRecipeId(recipe.getId()));
    }
}
