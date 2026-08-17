package com.application.recipesharing.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.recipesharing.dto.favorite.FavoriteResponse;
import com.application.recipesharing.dto.recipe.RecipeSummaryResponse;
import com.application.recipesharing.dto.user.ChangePasswordRequest;
import com.application.recipesharing.dto.user.ProfilePictureRequest;
import com.application.recipesharing.dto.user.UserProfileResponse;
import com.application.recipesharing.dto.user.UserUpdateRequest;
import com.application.recipesharing.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserProfileResponse getUserProfile(@PathVariable Long id) {
        return userService.getUserProfile(id);
    }

    @GetMapping("/{id}/recipes")
    public List<RecipeSummaryResponse> getRecipesByUser(@PathVariable Long id) {
        return userService.getRecipesByUser(id);
    }

    @GetMapping("/{id}/favorites")
    public List<FavoriteResponse> getFavoritesByUser(@PathVariable Long id) {
        return userService.getFavoritesByUser(id);
    }

    @PutMapping("/me")
    public UserProfileResponse updateProfile(@Valid @RequestBody UserUpdateRequest request,
                                             @RequestHeader("X-User-Id") Long userId) {
        return userService.updateProfile(userId, request);
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                               @RequestHeader("X-User-Id") Long userId) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/profile-picture")
    public UserProfileResponse uploadProfilePicture(@Valid @RequestBody ProfilePictureRequest request,
                                                    @RequestHeader("X-User-Id") Long userId) {
        return userService.uploadProfilePicture(userId, request.getImageUrl());
    }
}
