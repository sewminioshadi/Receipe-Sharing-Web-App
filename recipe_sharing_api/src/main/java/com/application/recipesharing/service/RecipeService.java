package com.application.recipesharing.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.recipesharing.dto.recipe.RecipeCreateRequest;
import com.application.recipesharing.dto.recipe.RecipeResponse;
import com.application.recipesharing.dto.recipe.RecipeSummaryResponse;
import com.application.recipesharing.dto.recipe.RecipeUpdateRequest;
import com.application.recipesharing.entity.Category;
import com.application.recipesharing.entity.Ingredient;
import com.application.recipesharing.entity.Recipe;
import com.application.recipesharing.entity.User;
import com.application.recipesharing.enums.Difficulty;
import com.application.recipesharing.exception.ResourceNotFoundException;
import com.application.recipesharing.exception.UnauthorizedException;
import com.application.recipesharing.mapper.IngredientMapper;
import com.application.recipesharing.mapper.RecipeMapper;
import com.application.recipesharing.repository.CategoryRepository;
import com.application.recipesharing.repository.LikeRepository;
import com.application.recipesharing.repository.RatingRepository;
import com.application.recipesharing.repository.RecipeRepository;
import com.application.recipesharing.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RatingRepository ratingRepository;
    private final LikeRepository likeRepository;

    @Transactional(readOnly = true)
    public List<RecipeSummaryResponse> getAllRecipes() {
        return recipeRepository.findAll().stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public RecipeResponse getRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
        return toDetail(recipe);
    }

    @Transactional
    public RecipeResponse createRecipe(RecipeCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Recipe recipe = Recipe.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .instructions(request.getInstructions())
                .cookingTime(request.getCookingTime())
                .difficulty(request.getDifficulty())
                .servings(request.getServings())
                .imageUrl(request.getImageUrl())
                .user(user)
                .category(category)
                .build();

        request.getIngredients().forEach(ingredientRequest -> {
            Ingredient ingredient = IngredientMapper.toEntity(ingredientRequest);
            ingredient.setRecipe(recipe);
            recipe.getIngredients().add(ingredient);
        });

        return toDetail(recipeRepository.save(recipe));
    }

    @Transactional
    public RecipeResponse updateRecipe(Long id, RecipeUpdateRequest request, Long userId) {
        Recipe existing = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));

        if (!existing.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this recipe");
        }

        if (request.getTitle() != null) {
            existing.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getInstructions() != null) {
            existing.setInstructions(request.getInstructions());
        }
        if (request.getCookingTime() != null) {
            existing.setCookingTime(request.getCookingTime());
        }
        if (request.getDifficulty() != null) {
            existing.setDifficulty(request.getDifficulty());
        }
        if (request.getServings() != null) {
            existing.setServings(request.getServings());
        }
        if (request.getImageUrl() != null) {
            existing.setImageUrl(request.getImageUrl());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            existing.setCategory(category);
        }

        if (request.getIngredients() != null) {
            existing.getIngredients().clear();
            request.getIngredients().forEach(ingredientRequest -> {
                Ingredient ingredient = IngredientMapper.toEntity(ingredientRequest);
                ingredient.setRecipe(existing);
                existing.getIngredients().add(ingredient);
            });
        }

        return toDetail(recipeRepository.save(existing));
    }

    @Transactional
    public void deleteRecipe(Long id, Long userId) {
        Recipe existing = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));

        if (!existing.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this recipe");
        }

        recipeRepository.delete(existing);
    }

    @Transactional(readOnly = true)
    public List<RecipeSummaryResponse> searchRecipes(String keyword) {
        return recipeRepository.findByTitleContainingIgnoreCase(keyword).stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public List<RecipeSummaryResponse> getRecipesByCategory(Long categoryId) {
        return recipeRepository.findByCategoryId(categoryId).stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public List<RecipeSummaryResponse> getRecipesByDifficulty(Difficulty difficulty) {
        return recipeRepository.findByDifficulty(difficulty).stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public List<RecipeSummaryResponse> getRecipesByCookingTime(Integer maxTime) {
        return recipeRepository.findByCookingTimeLessThanEqual(maxTime).stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public List<RecipeSummaryResponse> getLatestRecipes() {
        return recipeRepository.findTop10ByOrderByCreatedAtDesc().stream().map(this::toSummary).toList();
    }

    private RecipeSummaryResponse toSummary(Recipe recipe) {
        return RecipeMapper.toSummaryResponse(recipe, averageRating(recipe.getId()), likeCount(recipe.getId()));
    }

    private RecipeResponse toDetail(Recipe recipe) {
        return RecipeMapper.toResponse(recipe, averageRating(recipe.getId()), likeCount(recipe.getId()));
    }

    private Double averageRating(Long recipeId) {
        return ratingRepository.getAverageScoreByRecipeId(recipeId);
    }

    private long likeCount(Long recipeId) {
        return likeRepository.countByRecipeId(recipeId);
    }
}
