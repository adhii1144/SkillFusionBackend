package com.example.talenttrove.controller;

import com.example.talenttrove.doa.Users_Repo;
import com.example.talenttrove.model.Users;
import com.example.talenttrove.service.JwtService;
import com.example.talenttrove.service.Users_service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("skill-fusion")
public class Users_controller {

    @Autowired
    private Users_service usersService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private Users_Repo usersRepo;

    @PostMapping("/register")
    private ResponseEntity<Users> register(@RequestBody Users user) {
       return usersService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String , String >> login(@RequestBody Users user){
        Authentication authentication = authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword()));
        if (authentication.isAuthenticated()){
            user.setLastLogin(LocalDateTime.now());
            String token = jwtService.generateToken(user.getEmail());
            Map<String , String> response = new HashMap<>();
            response.put("token",token);
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getAllUsers(@RequestHeader("Authorization") String token,
                                                   @RequestParam(value = "query", required = false) String query,
                                                   @RequestParam(value = "bio", required = false) String bio,
                                                   @RequestParam(value = "address", required = false) String address) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        List<Users> users = usersService.getAllUsers(jwtToken, query, bio, address);
        return ResponseEntity.ok(users);
    }

}
