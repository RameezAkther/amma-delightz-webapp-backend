package com.ammadelightz.webapp_backend.service;

import com.ammadelightz.webapp_backend.model.Favorite;
import com.ammadelightz.webapp_backend.model.Recipe;
import com.ammadelightz.webapp_backend.repository.FavoriteRepository;
import com.ammadelightz.webapp_backend.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepo;
    private final RecipeRepository recipeRepo;

    public FavoriteService(FavoriteRepository favoriteRepo, RecipeRepository recipeRepo) {
        this.favoriteRepo = favoriteRepo;
        this.recipeRepo = recipeRepo;
    }

    public List<Favorite> getUserFavorites(String userId) {
        return favoriteRepo.findByUserId(userId);
    }

    public Favorite addToFavorites(String userId, String recipeId) {
        return favoriteRepo.findByUserIdAndRecipeId(userId, recipeId)
                .orElseGet(() -> {
                    Favorite fav = new Favorite();
                    fav.setUserId(userId);
                    fav.setRecipeId(recipeId);
                    return favoriteRepo.save(fav);
                });
    }

    public void removeFromFavorites(String userId, String recipeId) {
        favoriteRepo.findByUserIdAndRecipeId(userId, recipeId)
                .ifPresent(favoriteRepo::delete);
    }

    // ✅ ✅ ✅ PAGINATED FAVORITES (MAIN LOGIC)
    public Map<String, Object> getUserFavoriteRecipes(String userId, int page, int limit) {

        List<Favorite> favs = favoriteRepo.findByUserId(userId);

        List<String> ids = favs.stream()
                .map(Favorite::getRecipeId)
                .toList();

        List<Recipe> recipes = recipeRepo.findAllById(ids);

        int total = recipes.size();
        int totalPages = (int) Math.ceil((double) total / limit);

        int start = (page - 1) * limit;
        int end = Math.min(start + limit, total);

        List<Recipe> pageData =
                start < total ? recipes.subList(start, end) : List.of();

        return Map.of(
                "recipes", pageData,
                "total", total,
                "page", page,
                "pages", totalPages
        );
    }
}

