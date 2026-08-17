package com.application.recipesharing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.recipesharing.dto.common.BooleanResponse;
import com.application.recipesharing.dto.common.CountResponse;
import com.application.recipesharing.service.LikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recipes/{recipeId}")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/like")
    public BooleanResponse toggleLike(@PathVariable Long recipeId,
                                      @RequestHeader("X-User-Id") Long userId) {
        return likeService.toggleLike(recipeId, userId);
    }

    @GetMapping("/likes/count")
    public CountResponse getLikeCount(@PathVariable Long recipeId) {
        return likeService.getLikeCount(recipeId);
    }

    @GetMapping("/likes/status")
    public BooleanResponse isLiked(@PathVariable Long recipeId,
                                   @RequestHeader("X-User-Id") Long userId) {
        return likeService.isLiked(recipeId, userId);
    }
}
