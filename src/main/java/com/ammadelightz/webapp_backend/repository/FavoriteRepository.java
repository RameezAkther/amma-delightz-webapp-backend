package com.ammadelightz.webapp_backend.repository;

import com.ammadelightz.webapp_backend.model.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends MongoRepository<Favorite, String> {
    List<Favorite> findByUserId(String userId);
    Optional<Favorite> findByUserIdAndRecipeId(String userId, String recipeId);
}
