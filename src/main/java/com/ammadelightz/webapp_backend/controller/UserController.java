package com.ammadelightz.webapp_backend.controller;

import com.ammadelightz.webapp_backend.model.User;
import com.ammadelightz.webapp_backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 🟢 Get user by ID
    @GetMapping("/{userId}")
    public User getUserById(@PathVariable String userId) {
        return userService.getUserById(userId);
    }

    // 🟡 Change username
    @PutMapping("/{userId}/username")
    public User changeUsername(@PathVariable String userId, @RequestBody Map<String, String> req) {
        String newUsername = req.get("username");
        return userService.changeUsername(userId, newUsername);
    }

    // 🟠 Change password
    @PutMapping("/{userId}/password")
    public String changePassword(@PathVariable String userId, @RequestBody Map<String, String> req) {
        String oldPassword = req.get("oldPassword");
        String newPassword = req.get("newPassword");
        return userService.changePassword(userId, oldPassword, newPassword);
    }

    // 🔵 Change profile picture
    @PutMapping("/{userId}/avatar")
    public User changeAvatar(@PathVariable String userId, @RequestBody Map<String, String> req) {
        String newAvatar = req.get("avatar");
        return userService.changeAvatar(userId, newAvatar);
    }

    // 🟣 Change bio
    @PutMapping("/{userId}/bio")
    public User changeBio(@PathVariable String userId, @RequestBody Map<String, String> req) {
        return userService.changeBio(userId, req.get("bio"));
    }

    // 🔵 Change location
    @PutMapping("/{userId}/location")
    public User changeLocation(@PathVariable String userId, @RequestBody Map<String, String> req) {
        return userService.changeLocation(userId, req.get("location"));
    }

    // 🟢 Change preferences
    @PutMapping("/{userId}/preferences")
    public User changePreferences(@PathVariable String userId, @RequestBody User.Preferences preferences) {
        return userService.changePreferences(userId, preferences);
    }

}
