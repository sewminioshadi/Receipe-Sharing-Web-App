package com.application.recipesharing.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.recipesharing.dto.favorite.FavoriteResponse;
import com.application.recipesharing.dto.recipe.RecipeSummaryResponse;
import com.application.recipesharing.dto.user.ChangePasswordRequest;
import com.application.recipesharing.dto.user.UserProfileResponse;
import com.application.recipesharing.dto.user.UserRegistrationRequest;
import com.application.recipesharing.dto.user.UserUpdateRequest;
import com.application.recipesharing.entity.Recipe;
import com.application.recipesharing.entity.User;
import com.application.recipesharing.exception.DuplicateResourceException;
import com.application.recipesharing.exception.ResourceNotFoundException;
import com.application.recipesharing.exception.UnauthorizedException;
import com.application.recipesharing.mapper.RecipeMapper;
import com.application.recipesharing.mapper.UserMapper;
import com.application.recipesharing.repository.LikeRepository;
import com.application.recipesharing.repository.RatingRepository;
import com.application.recipesharing.repository.RecipeRepository;
import com.application.recipesharing.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final FavoriteService favoriteService;
    private final RatingRepository ratingRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public UserProfileResponse registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        return UserMapper.toProfileResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserMapper.toProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long id, UserUpdateRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (request.getFirstName() != null) {
            existing.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            existing.setLastName(request.getLastName());
        }
        if (request.getProfilePictureUrl() != null) {
            existing.setProfilePictureUrl(request.getProfilePictureUrl());
        }
        return UserMapper.toProfileResponse(userRepository.save(existing));
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!existing.getPassword().equals(request.getCurrentPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        existing.setPassword(request.getNewPassword());
        userRepository.save(existing);
    }

    @Transactional
    public UserProfileResponse uploadProfilePicture(Long id, String imageUrl) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        existing.setProfilePictureUrl(imageUrl);
        return UserMapper.toProfileResponse(userRepository.save(existing));
    }

    @Transactional(readOnly = true)
    public List<RecipeSummaryResponse> getRecipesByUser(Long userId) {
        return recipeRepository.findByUserId(userId).stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> getFavoritesByUser(Long userId) {
        return favoriteService.getFavoritesByUser(userId);
    }

    private RecipeSummaryResponse toSummary(Recipe recipe) {
        return RecipeMapper.toSummaryResponse(recipe, ratingRepository.getAverageScoreByRecipeId(recipe.getId()), likeRepository.countByRecipeId(recipe.getId()));
    }
}
