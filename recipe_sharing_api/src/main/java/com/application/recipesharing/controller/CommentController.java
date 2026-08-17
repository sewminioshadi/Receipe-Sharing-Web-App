package com.application.recipesharing.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.application.recipesharing.dto.comment.CommentRequest;
import com.application.recipesharing.dto.comment.CommentResponse;
import com.application.recipesharing.dto.comment.CommentUpdateRequest;
import com.application.recipesharing.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/recipes/{recipeId}/comments")
    public List<CommentResponse> getCommentsByRecipe(@PathVariable Long recipeId) {
        return commentService.getCommentsByRecipe(recipeId);
    }

    @PostMapping("/api/recipes/{recipeId}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long recipeId,
                                                      @Valid @RequestBody CommentRequest request,
                                                      @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(recipeId, request, userId));
    }

    @PutMapping("/api/comments/{id}")
    public CommentResponse updateComment(@PathVariable Long id,
                                         @Valid @RequestBody CommentUpdateRequest request,
                                         @RequestHeader("X-User-Id") Long userId) {
        return commentService.updateComment(id, request, userId);
    }

    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id,
                                              @RequestHeader("X-User-Id") Long userId) {
        commentService.deleteComment(id, userId);
        return ResponseEntity.noContent().build();
    }
}
