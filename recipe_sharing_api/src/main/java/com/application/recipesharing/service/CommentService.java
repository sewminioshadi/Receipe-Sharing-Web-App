package com.application.recipesharing.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.recipesharing.dto.comment.CommentRequest;
import com.application.recipesharing.dto.comment.CommentResponse;
import com.application.recipesharing.dto.comment.CommentUpdateRequest;
import com.application.recipesharing.entity.Comment;
import com.application.recipesharing.entity.Recipe;
import com.application.recipesharing.entity.User;
import com.application.recipesharing.exception.ResourceNotFoundException;
import com.application.recipesharing.exception.UnauthorizedException;
import com.application.recipesharing.mapper.CommentMapper;
import com.application.recipesharing.repository.CommentRepository;
import com.application.recipesharing.repository.RecipeRepository;
import com.application.recipesharing.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByRecipe(Long recipeId) {
        return commentRepository.findByRecipeId(recipeId).stream().map(CommentMapper::toResponse).toList();
    }

    @Transactional
    public CommentResponse addComment(Long recipeId, CommentRequest request, Long userId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .recipe(recipe)
                .user(user)
                .build();

        return CommentMapper.toResponse(commentRepository.save(comment));
    }

    @Transactional
    public CommentResponse updateComment(Long id, CommentUpdateRequest request, Long userId) {
        Comment existing = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        if (!existing.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this comment");
        }

        existing.setContent(request.getContent());
        return CommentMapper.toResponse(commentRepository.save(existing));
    }

    @Transactional
    public void deleteComment(Long id, Long userId) {
        Comment existing = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        if (!existing.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this comment");
        }

        commentRepository.delete(existing);
    }
}
