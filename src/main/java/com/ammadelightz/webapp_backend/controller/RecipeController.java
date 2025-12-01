package com.ammadelightz.webapp_backend.controller;

import com.ammadelightz.webapp_backend.model.Recipe;
import com.ammadelightz.webapp_backend.service.RecipeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "*")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // Paginated and metadata-only
    @GetMapping
    public Map<String, Object> getAllRecipes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int limit,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String ingredient,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sort
    ) {
        return recipeService.getPaginatedRecipes(page, limit, q, ingredient, cuisine, category, sort);
    }


    // Homepage top recipes - metadata only
    @GetMapping("/homepage")
    public List<Map<String, Object>> getTopRecipesForHomePage() {
        return recipeService.getTopRatedRecipes(3);
    }

    // Full recipe + increment view count
    @GetMapping("/{id}")
    public Recipe getRecipeById(@PathVariable String id) {
        return recipeService.getRecipeById(id);
    }

    @PostMapping
    public Recipe addRecipe(@RequestHeader("Authorization") String token, @RequestBody Recipe recipe) {
        return recipeService.addRecipe(recipe, token);
    }

    @PutMapping("/{id}")
    public Recipe updateRecipe(@RequestHeader("Authorization") String token, @PathVariable String id, @RequestBody Recipe recipe) {
        return recipeService.updateRecipe(id, recipe, token);
    }

    @DeleteMapping("/{id}")
    public void deleteRecipe(@RequestHeader("Authorization") String token, @PathVariable String id) {
        recipeService.deleteRecipe(id, token);
    }
}
