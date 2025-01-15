package com.example.talenttrove.controller;


import com.example.talenttrove.dto.UserProfile;
import com.example.talenttrove.model.Users;
import com.example.talenttrove.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("skill-fusion")
public class Profile_Controller {

    @Autowired
    private UserProfileService userProfileService;


    @GetMapping("/profile")
    public ResponseEntity<UserProfile> profile() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserProfile profile = userProfileService.getProfile(email);
            if (profile != null) {
                return ResponseEntity.ok(profile);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("profile/update")
    public ResponseEntity<Users> updateProfile(@RequestBody UserProfile userProfile) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            if (!userProfile.getEmail().equals(email)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            Users updatedProfile = userProfileService.updateProfile(userProfile);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}