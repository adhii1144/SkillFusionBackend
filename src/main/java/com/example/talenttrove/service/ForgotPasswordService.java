package com.example.talenttrove.service;

import com.example.talenttrove.doa.ForgotPasswordRepo;
import com.example.talenttrove.doa.Users_Repo;
import com.example.talenttrove.dto.MailBody;
import com.example.talenttrove.model.ChangePassword;
import com.example.talenttrove.model.ForgotPassword;
import com.example.talenttrove.model.Users;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
public class ForgotPasswordService {

    private final Users_Repo signinRepo;
    private final ForgotPasswordRepo forgotPasswordRepo;
    private final MailService mailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private static final long OTP_EXPIRY_DURATION = 70 * 1000; // 70 seconds

    public ForgotPasswordService(Users_Repo signinRepo, ForgotPasswordRepo forgotPasswordRepo, MailService mailService) {
        this.signinRepo = signinRepo;
        this.forgotPasswordRepo = forgotPasswordRepo;
        this.mailService = mailService;
    }

    public void initiatePasswordReset(String email) {
        Optional<Users> user = signinRepo.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        Integer otp = generateOtp();
        ForgotPassword forgotPassword = createForgotPassword(otp, user.get());
        MailBody mailBody = createMailBody(email, otp);

        mailService.sendSimpleMail(mailBody);
        forgotPasswordRepo.save(forgotPassword);
    }

    private ForgotPassword createForgotPassword(Integer otp, Users user) {
        return new ForgotPassword(otp, new Date(System.currentTimeMillis() + OTP_EXPIRY_DURATION), user);
    }

    private MailBody createMailBody(String email, Integer otp) {
        return new MailBody(email, "OTP for Password Reset", "This is your OTP for password reset: " + otp);
    }

    public void verifyOtp(String email, Integer otp) {
        Optional<Users> user = signinRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        ForgotPassword forgotPassword = forgotPasswordRepo.findByOtpAndUser_Email(otp, email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP or email."));

        if (forgotPassword.getOtpexpireyTime().before(new Date())) {
            forgotPasswordRepo.deleteById(forgotPassword.getFid());
            // Instead of throwing an exception, return a response with an expired message
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired.");
        }
    }

    public void changePassword(String email, ChangePassword changePassword) {
        if (!Objects.equals(changePassword.getPassword(), changePassword.getRepeatPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        Optional<Users> user = signinRepo.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        String hashPass = bCryptPasswordEncoder.encode(changePassword.getPassword());
        signinRepo.updatePassword(email, hashPass);
    }

    private Integer generateOtp() {
        return new Random().nextInt(900_000) + 100_000; // Ensures a 6-digit OTP
    }
}