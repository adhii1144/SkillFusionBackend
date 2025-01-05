package com.example.talenttrove.doa;

import com.example.talenttrove.model.ForgotPassword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForgotPasswordRepo extends JpaRepository<ForgotPassword, Integer> {
    Optional<ForgotPassword> findByOtpAndUser_Email(Integer otp, String email);
}