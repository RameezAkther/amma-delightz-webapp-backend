package com.ammadelightz.webapp_backend.controller;

import com.ammadelightz.webapp_backend.model.User;
import com.ammadelightz.webapp_backend.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // --- Signup ---
    @PostMapping("/signup")
    public User registerUser(@RequestBody User user) {
        return userService.register(user);
    }

    // --- Login ---
    // --- Login ---
    @PostMapping("/login")
    public Map<String, String> loginUser(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        return userService.loginWithUserInfo(email, password);
    }

    @GetMapping("/is-admin")
    public Map<String, Boolean> isAdmin(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        boolean isAdmin = userService.isAdmin(token);
        return Map.of("admin", isAdmin);
    }

}
