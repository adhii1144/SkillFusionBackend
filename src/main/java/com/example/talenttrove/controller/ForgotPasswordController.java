package com.example.talenttrove.controller;

import com.example.talenttrove.model.ChangePassword;
import com.example.talenttrove.service.ForgotPasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("skill-fusion/forgotPassword")
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    public ForgotPasswordController(ForgotPasswordService forgotPasswordService) {
        this.forgotPasswordService = forgotPasswordService;
    }

    @PostMapping("/verifyEmail/{email}")
    public ResponseEntity<?> verifyEmail(@PathVariable String email) {
        try {
            forgotPasswordService.initiatePasswordReset(email);
            return ResponseEntity.ok(Map.of("message", "OTP sent to the email."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "Failed to send OTP. Please try again.")
            );
        }
    }

    @PostMapping("/verify/{otp}/{email}")
    public ResponseEntity<?> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        try {
            forgotPasswordService.verifyOtp(email, otp);
            return ResponseEntity.ok(Map.of("message", "OTP verified successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("message", "Invalid OTP. Please try again.")
            );
        }
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<?> changePassword(@PathVariable String email, @RequestBody ChangePassword changePassword) {
        try {
            forgotPasswordService.changePassword(email, changePassword);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "Failed to change the password. Please try again.")
            );
        }
    }

}
