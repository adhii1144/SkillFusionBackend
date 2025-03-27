package com.example.talenttrove.model;

import com.example.talenttrove.dto.RequestStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = {"connectedUsers", "forgotPassword", "profileImage"})
@EqualsAndHashCode(of = "id")
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_connections",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "connected_user_id")
    )
    @JsonIgnore
    private Set<Users> connectedUsers = new HashSet<>();

    @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<ConnectionRequest> sentRequests = new HashSet<>();

    @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<ConnectionRequest> receivedRequests = new HashSet<>();

    public void removeConnection(Users user) {
        this.connectedUsers.remove(user);
        user.getConnectedUsers().remove(this);
    }

    public void addConnection(Users user) {
        this.connectedUsers.add(user);
        user.getConnectedUsers().add(this);
    }

    @Lob
    @Column(name = "profile_image", columnDefinition = "LONGBLOB")
    private byte[] profileImage;

    private LocalDateTime lastPasswordChangeTime;
    private int passwordChangeCountThisWeek;

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skillJson;

    @JsonIgnore
    @OneToOne(mappedBy = "user")
    private ForgotPassword forgotPassword;

    public List<Skill> getSkills() {
        if (this.skillJson == null) {
            return new ArrayList<>();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(this.skillJson, new TypeReference<List<Skill>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void setSkills(List<Skill> skillsList) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            this.skillJson = objectMapper.writeValueAsString(skillsList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public boolean hasConnectionWith(Users other) {
        return this.connectedUsers.contains(other);
    }

    public boolean hasPendingRequestWith(Users other) {
        return this.sentRequests.stream()
                .anyMatch(request -> request.getToUser().equals(other) && request.getStatus() == RequestStatus.PENDING) ||
                this.receivedRequests.stream()
                        .anyMatch(request -> request.getFromUser().equals(other) && request.getStatus() == RequestStatus.PENDING);
    }
}