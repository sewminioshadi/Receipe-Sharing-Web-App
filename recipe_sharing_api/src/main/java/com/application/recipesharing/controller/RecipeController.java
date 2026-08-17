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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.recipesharing.dto.recipe.RecipeCreateRequest;
import com.application.recipesharing.dto.recipe.RecipeResponse;
import com.application.recipesharing.dto.recipe.RecipeSummaryResponse;
import com.application.recipesharing.dto.recipe.RecipeUpdateRequest;
import com.application.recipesharing.enums.Difficulty;
import com.application.recipesharing.service.RecipeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public List<RecipeSummaryResponse> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @GetMapping("/latest")
    public List<RecipeSummaryResponse> getLatestRecipes() {
        return recipeService.getLatestRecipes();
    }

    @GetMapping("/search")
    public List<RecipeSummaryResponse> searchRecipes(@RequestParam String keyword) {
        return recipeService.searchRecipes(keyword);
    }

    @GetMapping("/category/{categoryId}")
    public List<RecipeSummaryResponse> getRecipesByCategory(@PathVariable Long categoryId) {
        return recipeService.getRecipesByCategory(categoryId);
    }

    @GetMapping("/difficulty/{difficulty}")
    public List<RecipeSummaryResponse> getRecipesByDifficulty(@PathVariable Difficulty difficulty) {
        return recipeService.getRecipesByDifficulty(difficulty);
    }

    @GetMapping("/cooking-time")
    public List<RecipeSummaryResponse> getRecipesByCookingTime(@RequestParam Integer maxTime) {
        return recipeService.getRecipesByCookingTime(maxTime);
    }

    @GetMapping("/{id}")
    public RecipeResponse getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id);
    }

    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(@Valid @RequestBody RecipeCreateRequest request,
                                                       @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recipeService.createRecipe(request, userId));
    }

    @PutMapping("/{id}")
    public RecipeResponse updateRecipe(@PathVariable Long id,
                                       @Valid @RequestBody RecipeUpdateRequest request,
                                       @RequestHeader("X-User-Id") Long userId) {
        return recipeService.updateRecipe(id, request, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id,
                                             @RequestHeader("X-User-Id") Long userId) {
        recipeService.deleteRecipe(id, userId);
        return ResponseEntity.noContent().build();
    }
}
