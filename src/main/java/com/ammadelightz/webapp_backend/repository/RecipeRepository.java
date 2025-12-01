package com.ammadelightz.webapp_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.ammadelightz.webapp_backend.model.Recipe;

import java.util.List;

public interface RecipeRepository extends MongoRepository<Recipe, String> {
    // Custom queries can be added later
    List<Recipe> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
}
