package com.example.talenttrove.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String mobile;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String bio;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String website;

    private LocalDateTime lastLogin;

    public LocalDateTime getLastPasswordChangeTime() {
        return lastPasswordChangeTime;
    }

    public void setLastPasswordChangeTime(LocalDateTime lastPasswordChangeTime) {
        this.lastPasswordChangeTime = lastPasswordChangeTime;
    }

    public int getPasswordChangeCountThisWeek() {
        return passwordChangeCountThisWeek;
    }

    public void setPasswordChangeCountThisWeek(int passwordChangeCountThisWeek) {
        this.passwordChangeCountThisWeek = passwordChangeCountThisWeek;
    }

    private LocalDateTime lastPasswordChangeTime;
    private int passwordChangeCountThisWeek;

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skillJson;



    public void setSkills(List<Skill> skillsList) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            this.skillJson = objectMapper.writeValueAsString(skillsList); // Convert list to JSON string
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public List<Skill> getSkills() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(this.skillJson, new TypeReference<List<Skill>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @JsonIgnore
    @OneToOne(mappedBy = "user")
    private ForgotPassword forgotPassword;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getSkillJson() {
        return skillJson;
    }

    public void setSkillJson(String skills) {
        this.skillJson = skills;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
}