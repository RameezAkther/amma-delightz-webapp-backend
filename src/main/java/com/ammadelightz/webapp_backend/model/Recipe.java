package com.ammadelightz.webapp_backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "recipe")
public class Recipe {

    @Id
    private String id;

    private String title;
    private String description;
    private String cuisine;
    private String category;

    private List<Ingredient> ingredients;
    private List<String> steps;

    private int prepTime;
    private int cookTime;
    private int totalTime;
    private int servings;
    private List<String> tags;

    private CreatedBy createdBy;

    private Instant createdAt;
    private Instant updatedAt;

    private int ratingsCount;
    private double averageRating;
    private int favoritesCount;
    private int views;
    private List<String> imageUrl;
}
