package com.application.recipesharing.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.application.recipesharing.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByRecipeId(Long recipeId);
    List<Comment> findByUserId(Long userId);
    void deleteByRecipeId(Long recipeId);
}
