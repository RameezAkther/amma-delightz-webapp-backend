package com.ammadelightz.webapp_backend.service;

import com.ammadelightz.webapp_backend.model.Recipe;
import com.ammadelightz.webapp_backend.repository.RecipeRepository;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RecipeRepository recipeRepo;

    public ChatService(RecipeRepository recipeRepo) {
        this.recipeRepo = recipeRepo;
    }

    // ✅ MAIN CHAT LOGIC
    public String chatWithBot(String userMessage) {

        // ✅ GLOBAL SYSTEM PROMPT (ALWAYS APPLIED)
        String systemPrompt = """
        You are the official AI assistant for "Amma Delightz", a recipe-based web application.

        About the app:
        - Amma Delightz is a recipe sharing web application.
        - Recipes are uploaded by the admin or owner.
        - Users can create accounts, log in, and browse recipes.
        - The app focuses on home-style cooking and traditional recipes.

        Your behavior rules:
        - If the user asks about the app, clearly explain Amma Delightz.
        - If the user asks about a recipe that exists, use database recipes.
        - If no recipe matches, answer using your own cooking knowledge.
        - Be friendly, concise, and helpful.
        """;

        // ✅ STEP 1: Search recipes from DB
        List<Recipe> matchedRecipes =
                recipeRepo.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        userMessage, userMessage
                );

        String finalPrompt;

        // ✅ STEP 2: If recipes exist → inject recipe context
        if (!matchedRecipes.isEmpty()) {

            String recipeContext = matchedRecipes.stream()
                    .limit(3)
                    .map(r -> String.format(
                            """
                            Recipe Title: %s
                            Description: %s
                            Cuisine: %s
                            Category: %s
                            """,
                            r.getTitle(),
                            r.getDescription(),
                            r.getCuisine(),
                            r.getCategory()
                    ))
                    .collect(Collectors.joining("\n"));

            finalPrompt = """
                %s

                Here are some relevant recipes from the database:
                ======================
                %s
                ======================

                User Question: %s
                """.formatted(systemPrompt, recipeContext, userMessage);

        } else {
            // ✅ STEP 3: NO RECIPE FOUND → still keep system context
            finalPrompt = """
                %s

                User Question: %s
                """.formatted(systemPrompt, userMessage);
        }

        return callGemini(finalPrompt);
    }


    // ✅ STEP 4: GEMINI SDK CALL (NO HTTP ERRORS EVER)
    private String callGemini(String prompt) {
        try {
            Client client = Client.builder()
                    .apiKey(geminiApiKey)
                    .build();

            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-2.5-flash",  // ✅ SAFE & FAST MODEL
                            prompt,
                            null
                    );

            return response.text();

        } catch (Exception e) {
            e.printStackTrace();
            return "⚠️ AI service is currently unavailable. Please try again later.";
        }
    }
}
