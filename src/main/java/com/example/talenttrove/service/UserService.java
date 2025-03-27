package com.example.talenttrove.service;

import com.example.talenttrove.doa.Users_Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private Users_Repo userRepository;

    public String getEmailById(int userId) {
        return userRepository.findById(userId)
                .map(user -> user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
