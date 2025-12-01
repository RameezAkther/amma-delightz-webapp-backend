package com.ammadelightz.webapp_backend.service;

import com.ammadelightz.webapp_backend.model.User;
import com.ammadelightz.webapp_backend.repository.UserRepository;
import com.ammadelightz.webapp_backend.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // ✅ Login user and return token + userId + role
    public Map<String, String> loginWithUserInfo(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty())
            throw new RuntimeException("User not found");

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPasswordHash()))
            throw new RuntimeException("Invalid credentials");

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return Map.of(
                "token", token,
                "userId", user.getId(),
                "role", user.getRole()
        );
    }


    // ✅ Register new user
    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new RuntimeException("Email already registered");

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    // ✅ Login user and return JWT
    public String login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty())
            throw new RuntimeException("User not found");

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash()))
            throw new RuntimeException("Invalid credentials");

        return jwtUtil.generateToken(user.getEmail(), user.getRole());
    }

    // ✅ Find user by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // ✅ Fetch user by ID
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ Change username
    public User changeUsername(String userId, String newUsername) {
        User user = getUserById(userId);
        user.setUsername(newUsername);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    // ✅ Change password
    public String changePassword(String userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Old password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        return "Password updated successfully";
    }

    // ✅ Change avatar
    public User changeAvatar(String userId, String newAvatar) {
        User user = getUserById(userId);

        // Ensure profile is not null
        if (user.getProfile() == null) {
            user.setProfile(new User.Profile());
        }

        user.getProfile().setAvatar(newAvatar);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    public boolean isAdmin(String token) {
        try {
            String email = jwtUtil.extractUsername(token);

            if (email == null || email.isEmpty()) {
                return false;
            }

            return userRepository.findByEmail(email)
                    .map(user -> "ADMIN".equals(user.getRole()))
                    .orElse(false);

        } catch (Exception e) {
            return false;
        }
    }

    // ✅ Change Bio
    public User changeBio(String userId, String newBio) {
        User user = getUserById(userId);

        if (user.getProfile() == null) {
            user.setProfile(new User.Profile());
        }

        user.getProfile().setBio(newBio);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    // ✅ Change Location
    public User changeLocation(String userId, String newLocation) {
        User user = getUserById(userId);

        if (user.getProfile() == null) {
            user.setProfile(new User.Profile());
        }

        user.getProfile().setLocation(newLocation);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    // ✅ Change Preferences
    public User changePreferences(String userId, User.Preferences preferences) {
        User user = getUserById(userId);
        user.setPreferences(preferences);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }
}
