package com.ammadelightz.webapp_backend.controller;

import com.ammadelightz.webapp_backend.service.ChatService;
import com.ammadelightz.webapp_backend.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;
    private final JwtUtil jwtUtil;

    public ChatController(ChatService chatService, JwtUtil jwtUtil) {
        this.chatService = chatService;
        this.jwtUtil = jwtUtil;
    }

    // ✅ Only logged in users can chat
    @PostMapping
    public Map<String, String> chat(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body
    ) {
        String token = authHeader.replace("Bearer ", "");

        // ✅ Validate token
        String email = jwtUtil.extractUsername(token);
        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Unauthorized");
        }

        String userMessage = body.get("message");

        String botReply = chatService.chatWithBot(userMessage);

        return Map.of("reply", botReply);
    }
}
