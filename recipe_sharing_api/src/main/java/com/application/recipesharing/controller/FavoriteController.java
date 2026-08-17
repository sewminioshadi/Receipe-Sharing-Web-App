package com.application.recipesharing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.recipesharing.dto.common.BooleanResponse;
import com.application.recipesharing.service.FavoriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recipes/{recipeId}")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/favorite")
    public BooleanResponse toggleFavorite(@PathVariable Long recipeId,
                                          @RequestHeader("X-User-Id") Long userId) {
        return favoriteService.toggleFavorite(recipeId, userId);
    }

    @GetMapping("/favorite/status")
    public BooleanResponse isFavorited(@PathVariable Long recipeId,
                                       @RequestHeader("X-User-Id") Long userId) {
        return favoriteService.isFavorited(recipeId, userId);
    }
}
