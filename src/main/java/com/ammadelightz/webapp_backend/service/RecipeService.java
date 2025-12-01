package com.ammadelightz.webapp_backend.service;

import com.ammadelightz.webapp_backend.model.Recipe;
import com.ammadelightz.webapp_backend.repository.RecipeRepository;
import com.ammadelightz.webapp_backend.security.JwtUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final JwtUtil jwtUtil;

    public RecipeService(RecipeRepository recipeRepository, JwtUtil jwtUtil) {
        this.recipeRepository = recipeRepository;
        this.jwtUtil = jwtUtil;
    }

    // --- Return only metadata for paginated results ---
    public Map<String, Object> getPaginatedRecipes(
            int page,
            int limit,
            String query,
            String ingredient,
            String cuisine,
            String category,
            String sort
    ) {
        // 1️⃣ Get all recipes (later we can optimize with repo-level filters)
        List<Recipe> allRecipes = recipeRepository.findAll();

        // 2️⃣ Apply filters in memory (good enough for your project size)

        // text search on title/description
        if (query != null && !query.trim().isEmpty()) {
            String qLower = query.toLowerCase();
            allRecipes = allRecipes.stream()
                    .filter(r ->
                            (r.getTitle() != null && r.getTitle().toLowerCase().contains(qLower)) ||
                                    (r.getDescription() != null && r.getDescription().toLowerCase().contains(qLower))
                    )
                    .collect(Collectors.toList());
        }

        // ingredient search
        if (ingredient != null && !ingredient.trim().isEmpty()) {
            String ingLower = ingredient.toLowerCase();
            allRecipes = allRecipes.stream()
                    .filter(r -> r.getIngredients() != null &&
                            r.getIngredients().stream().anyMatch(ing ->
                                    ing.getName() != null &&
                                            ing.getName().toLowerCase().contains(ingLower)
                            )
                    )
                    .collect(Collectors.toList());
        }

        // cuisine filter
        if (cuisine != null && !cuisine.trim().isEmpty()) {
            String cLower = cuisine.toLowerCase();
            allRecipes = allRecipes.stream()
                    .filter(r -> r.getCuisine() != null &&
                            r.getCuisine().toLowerCase().equals(cLower))
                    .collect(Collectors.toList());
        }

        // category filter
        if (category != null && !category.trim().isEmpty()) {
            String catLower = category.toLowerCase();
            allRecipes = allRecipes.stream()
                    .filter(r -> r.getCategory() != null &&
                            r.getCategory().toLowerCase().equals(catLower))
                    .collect(Collectors.toList());
        }

        // 3️⃣ Sorting
        if (sort != null && !sort.isEmpty()) {
            switch (sort.toLowerCase()) {
                case "rating":
                    allRecipes.sort(
                            Comparator.comparingDouble(Recipe::getAverageRating)
                                    .reversed()
                    );
                    break;
                case "views":
                    allRecipes.sort(
                            Comparator.comparingInt(Recipe::getViews).reversed()
                    );
                    break;
                case "newest":
                    allRecipes.sort(
                            Comparator.comparing(Recipe::getCreatedAt,
                                    Comparator.nullsLast(Comparator.naturalOrder())
                            ).reversed()
                    );
                    break;
                default:
                    // no sorting or default sort
            }
        }

        // 4️⃣ Convert to metadata
        List<Map<String, Object>> metaRecipes = allRecipes.stream()
                .map(this::convertToMeta)
                .collect(Collectors.toList());

        int total = metaRecipes.size();
        int totalPages = (int) Math.ceil((double) total / limit);
        int start = (page - 1) * limit;
        int end = Math.min(start + limit, total);

        List<Map<String, Object>> paginated =
                start < total ? metaRecipes.subList(start, end) : Collections.emptyList();

        Map<String, Object> response = new HashMap<>();
        response.put("recipes", paginated);
        response.put("total", total);
        response.put("page", page);
        response.put("pages", totalPages);
        response.put("query", query);
        response.put("ingredient", ingredient);
        response.put("cuisine", cuisine);
        response.put("category", category);
        response.put("sort", sort);

        return response;
    }



    // --- Return only top N recipes metadata ---
    public List<Map<String, Object>> getTopRatedRecipes(int limit) {
        return recipeRepository.findAll().stream()
                .sorted(Comparator.comparingDouble(Recipe::getAverageRating).reversed())
                .limit(limit)
                .map(this::convertToMeta)
                .collect(Collectors.toList());
    }

    // --- Increment views count and return full recipe ---
    public Recipe getRecipeById(String id) {
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe != null) {
            recipe.setViews(recipe.getViews() + 1);
            recipeRepository.save(recipe);
        }
        return recipe;
    }

    public Recipe addRecipe(Recipe recipe, String token) {
        ensureAdmin(token);
        recipe.setCreatedAt(Instant.now());
        recipe.setUpdatedAt(Instant.now());
        return recipeRepository.save(recipe);
    }

    public Recipe updateRecipe(String id, Recipe recipe, String token) {
        ensureAdmin(token);
        Recipe existing = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        recipe.setId(existing.getId());
        recipe.setUpdatedAt(Instant.now());
        return recipeRepository.save(recipe);
    }

    public void deleteRecipe(String id, String token) {
        ensureAdmin(token);
        recipeRepository.deleteById(id);
    }

    // 🧩 Role check helper
    private void ensureAdmin(String token) {
        System.out.println(token);
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid token");
        }

        String jwt = token.substring(7);
        String role = jwtUtil.extractRole(jwt);

        if (!"admin".equalsIgnoreCase(role)) {
            throw new RuntimeException("Access denied: Admins only");
        }
    }

    // --- Helper: convert full recipe to metadata-only map ---
    private Map<String, Object> convertToMeta(Recipe recipe) {
        Map<String, Object> meta = new HashMap<>();
        meta.put("id", recipe.getId());
        meta.put("title", recipe.getTitle());
        meta.put("description", recipe.getDescription());
        meta.put("imageUrl", recipe.getImageUrl());
        meta.put("averageRating", recipe.getAverageRating());
        meta.put("views", recipe.getViews());
        meta.put("cuisine", recipe.getCuisine());
        meta.put("category", recipe.getCategory());
        return meta;
    }
}
