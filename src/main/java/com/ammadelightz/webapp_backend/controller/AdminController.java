package com.ammadelightz.webapp_backend.controller;

import com.ammadelightz.webapp_backend.model.User;
import com.ammadelightz.webapp_backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepo;

    public AdminController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // ✅ PAGINATED + SEARCH USERS
    @GetMapping("/users")
    public Map<String, Object> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int limit,
            @RequestParam(required = false) String q
    ) {
        List<User> allUsers;

        if (q != null && !q.isEmpty()) {
            allUsers = userRepo
                    .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q);
        } else {
            allUsers = userRepo.findAll();
        }

        // Remove password
        List<User> safeUsers = allUsers.stream()
                .peek(u -> u.setPasswordHash(null))
                .collect(Collectors.toList());

        int total = safeUsers.size();
        int totalPages = (int) Math.ceil((double) total / limit);

        int start = (page - 1) * limit;
        int end = Math.min(start + limit, total);

        List<User> paginated =
                start < total ? safeUsers.subList(start, end) : Collections.emptyList();

        Map<String, Object> res = new HashMap<>();
        res.put("users", paginated);
        res.put("total", total);
        res.put("page", page);
        res.put("pages", totalPages);

        return res;
    }

    // ✅ DELETE USER
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable String id) {
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
            return "User deleted successfully";
        } else {
            return "User not found";
        }
    }
}
