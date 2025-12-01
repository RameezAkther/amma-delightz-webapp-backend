package com.ammadelightz.webapp_backend.controller;

import com.ammadelightz.webapp_backend.model.Favorite;
import com.ammadelightz.webapp_backend.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // 🟢 Add to favorites
    @PostMapping
    public Favorite addToFavorites(@RequestBody Map<String, String> req) {
        String userId = req.get("userId");
        String recipeId = req.get("recipeId");
        return favoriteService.addToFavorites(userId, recipeId);
    }

    // 🟡 Get all favorites for a user
    @GetMapping("/{userId}")
    public List<Favorite> getUserFavorites(@PathVariable String userId) {
        return favoriteService.getUserFavorites(userId);
    }

    @GetMapping("/{userId}/paged")
    public Map<String, Object> getUserFavoritesPaged(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int limit
    ) {
        return favoriteService.getUserFavoriteRecipes(userId, page, limit);
    }


    // 🔴 Remove from favorites (optional)
    @DeleteMapping
    public String removeFromFavorites(@RequestBody Map<String, String> req) {
        String userId = req.get("userId");
        String recipeId = req.get("recipeId");
        favoriteService.removeFromFavorites(userId, recipeId);
        return "Removed from favorites";
    }
}
