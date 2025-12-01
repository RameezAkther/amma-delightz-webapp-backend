package com.ammadelightz.webapp_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String username;
    private String email;
    private String passwordHash;
    private String role = "user";

    private Profile profile = new Profile();        // ✅ ensure initialized
    private Preferences preferences = new Preferences(); // ✅ ensure initialized

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    // ✅ Embedded subdocuments
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Profile {
        private String name;
        private String avatar;
        private String bio;
        private String location;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Preferences {
        private List<String> diet;
        private List<String> favoriteCuisines;
    }
}
