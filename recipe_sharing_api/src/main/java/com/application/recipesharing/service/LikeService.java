package com.application.recipesharing.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.recipesharing.dto.common.BooleanResponse;
import com.application.recipesharing.dto.common.CountResponse;
import com.application.recipesharing.entity.Like;
import com.application.recipesharing.entity.Recipe;
import com.application.recipesharing.entity.User;
import com.application.recipesharing.exception.ResourceNotFoundException;
import com.application.recipesharing.repository.LikeRepository;
import com.application.recipesharing.repository.RecipeRepository;
import com.application.recipesharing.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Transactional
    public BooleanResponse toggleLike(Long recipeId, Long userId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (likeRepository.existsByUserIdAndRecipeId(userId, recipeId)) {
            likeRepository.deleteByUserIdAndRecipeId(userId, recipeId);
            return BooleanResponse.builder().value(false).build();
        }

        Like like = Like.builder()
                .recipe(recipe)
                .user(user)
                .build();
        likeRepository.save(like);
        return BooleanResponse.builder().value(true).build();
    }

    @Transactional(readOnly = true)
    public CountResponse getLikeCount(Long recipeId) {
        return CountResponse.builder()
                .value(likeRepository.countByRecipeId(recipeId))
                .build();
    }

    @Transactional(readOnly = true)
    public BooleanResponse isLiked(Long recipeId, Long userId) {
        return BooleanResponse.builder()
                .value(likeRepository.existsByUserIdAndRecipeId(userId, recipeId))
                .build();
    }
}
