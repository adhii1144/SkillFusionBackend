package com.example.talenttrove.controller;

import com.example.talenttrove.dto.UserProfile;
import com.example.talenttrove.model.Users;
import com.example.talenttrove.service.UserProfileService;
import com.example.talenttrove.service.Users_service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/skill-fusion/auth")
public class AuthController {

    @Autowired
    private Users_service users_service;

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getLoggedInUser() {
        // Get the authenticated user's email from the security context
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Authenticated Email: " + email); // ✅ Log authentication info

        // Fetch the user using email
        Users user = users_service.findByEmail(email);

        if (user == null) {
            System.out.println("User not found!"); // ✅ Log if no user is found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        // ✅ Log ID to confirm it's retrieved
        System.out.println("User ID: " + user.getId());

        // Prepare the response
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());  // ✅ Ensure ID is included
        userData.put("email", user.getEmail());

        return ResponseEntity.ok(userData);
    }


}
