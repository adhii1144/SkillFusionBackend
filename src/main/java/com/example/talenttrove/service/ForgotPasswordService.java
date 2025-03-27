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





//package com.example.talenttrove.service;
//
//import com.example.talenttrove.doa.ForgotPasswordRepo;
//import com.example.talenttrove.doa.Users_Repo;
//import com.example.talenttrove.dto.MailBody;
//import com.example.talenttrove.model.ChangePassword;
//import com.example.talenttrove.model.ForgotPassword;
//import com.example.talenttrove.model.Users;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.Date;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.Random;
//
//@Service
//public class ForgotPasswordService {
//
//    private final Users_Repo signinRepo;
//    private final ForgotPasswordRepo forgotPasswordRepo;
//    private final MailService mailService;
//    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//    private static final long OTP_EXPIRY_DURATION = 10 * 60 * 1000; // 10 minutes
//
//    public ForgotPasswordService(Users_Repo signinRepo, ForgotPasswordRepo forgotPasswordRepo, MailService mailService) {
//        this.signinRepo = signinRepo;
//        this.forgotPasswordRepo = forgotPasswordRepo;
//        this.mailService = mailService;
//    }
//
//    public void initiatePasswordReset(String email) {
//        Optional<Users> user = signinRepo.findByEmail(email);
//        if (user.isEmpty()) {
//            throw new UsernameNotFoundException("User not found with email: " + email);
//        }
//
//        Integer otp = generateOtp();
//        ForgotPassword forgotPassword = createForgotPassword(otp, user.get());
//
//        // Generate email content using the HTML template
//        String emailContent = generateHtmlEmail(user.get().getUsername(), otp);
//
//        MailBody mailBody = new MailBody(email, "Password Reset OTP", emailContent);
//        mailService.sendHtmlMail(mailBody);
//
//        forgotPasswordRepo.save(forgotPassword);
//    }
//
//    private ForgotPassword createForgotPassword(Integer otp, Users user) {
//        return new ForgotPassword(otp, new Date(System.currentTimeMillis() + OTP_EXPIRY_DURATION), user);
//    }
//
//    public void verifyOtp(String email, Integer otp) {
//        Optional<Users> user = signinRepo.findByEmail(email);
//        if (user.isEmpty()) {
//            throw new UsernameNotFoundException("User not found with email: " + email);
//        }
//
//        ForgotPassword forgotPassword = forgotPasswordRepo.findByOtpAndUser_Email(otp, email)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP or email."));
//
//        if (forgotPassword.getOtpexpireyTime().before(new Date())) {
//            forgotPasswordRepo.deleteById(forgotPassword.getFid());
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired.");
//        }
//    }
//
//    public void changePassword(String email, ChangePassword changePassword) {
//        if (!Objects.equals(changePassword.getPassword(), changePassword.getRepeatPassword())) {
//            throw new IllegalArgumentException("Passwords do not match.");
//        }
//
//        Optional<Users> user = signinRepo.findByEmail(email);
//        if (user.isEmpty()) {
//            throw new UsernameNotFoundException("User not found with email: " + email);
//        }
//
//        String hashPass = bCryptPasswordEncoder.encode(changePassword.getPassword());
//        signinRepo.updatePassword(email, hashPass);
//    }
//
//    private Integer generateOtp() {
//        return new Random().nextInt(900_000) + 100_000; // Ensures a 6-digit OTP
//    }
//
//    private String generateHtmlEmail(String username, Integer otp) {
//        return """
//            <!DOCTYPE html>
//            <html>
//            <head>
//                <meta charset="UTF-8">
//                <meta name="viewport" content="width=device-width, initial-scale=1.0">
//                <title>Password Reset</title>
//            </head>
//            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f3f4f6;">
//                <table role="presentation" style="width: 100%; max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">
//                    <tr>
//                        <td style="background: linear-gradient(to right, #2563eb, #1d4ed8); padding: 24px; text-align: center; color: white;">
//                            <h1 style="margin: 0; font-size: 24px; font-weight: bold;">Password Reset Request</h1>
//                        </td>
//                    </tr>
//                    <tr>
//                        <td style="padding: 32px;">
//                            <div style="text-align: center; margin-bottom: 32px;">
//                                <p style="color: #4b5563; font-size: 18px; margin-bottom: 16px;">
//                                    Hello <span style="font-weight: 600;">%s</span>,
//                                </p>
//                                <p style="color: #4b5563;">
//                                    Use the verification code below to proceed:
//                                </p>
//                            </div>
//                            <div style="background-color: #f9fafb; border-radius: 12px; padding: 24px; margin-bottom: 32px; text-align: center;">
//                                <p style="font-size: 32px; font-weight: bold; letter-spacing: 1rem; color: #2563eb; margin: 0;">
//                                    %d
//                                </p>
//                            </div>
//                            <p style="text-align: center; color: #4b5563;">This code will expire in 10 minutes.</p>
//                        </td>
//                    </tr>
//                </table>
//            </body>
//            </html>
//            """.formatted(username, otp);
//    }
//
//}
//
