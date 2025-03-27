package com.example.talenttrove.service;

import com.example.talenttrove.doa.Users_Repo;
import com.example.talenttrove.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class Users_service {
    @Autowired
    private Users_Repo usersRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtService jwtService;

    public ResponseEntity<Users> register(Users user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Users users = usersRepo.save(user);
        if (users == null) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }else{

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(users);
        }

    }

//    public List<Users> searchUsers(String query, String bio, String address) {
//        return usersRepo.findAll();
//    }

    public List<Users> getAllUsers(String token, String query, String bio, String address) {
        String email = jwtService.extractEmail(token);
        System.out.println("Logged-in user email: " + email); // Debugging log
        return usersRepo.searchUsers(query, bio, address, email);
    }

    private String getCurrentUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

        public Users findByEmail(String email) {
            return usersRepo.findByEmail(email).orElse(null);
        }

    }

