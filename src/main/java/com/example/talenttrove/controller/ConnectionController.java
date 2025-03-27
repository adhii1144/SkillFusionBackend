package com.example.talenttrove.controller;


import com.example.talenttrove.doa.ConnectionRequestRepository;
import com.example.talenttrove.doa.Users_Repo;
import com.example.talenttrove.dto.ConnectionRequestDTO;
import com.example.talenttrove.dto.UserProfile;
import com.example.talenttrove.model.ConnectionRequest;
import com.example.talenttrove.model.Users;
import com.example.talenttrove.service.ConnectionRequestService;
import com.example.talenttrove.service.EmailService;
import com.example.talenttrove.service.UserProfileService;
import com.example.talenttrove.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("skill-fusion/connections")
public class ConnectionController {

    @Autowired
    private ConnectionRequestService requestService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserService userService;

    @Autowired
    private Users_Repo usersRepo;

    @Autowired
    private ConnectionRequestRepository connectionRequestRepository;

    @Autowired
    private EmailService emailService;


    @PostMapping("/send-request")
    public ResponseEntity<?> sendConnectionRequest(@RequestBody ConnectionRequestDTO requestDTO) {
        try {
            // Validate input
            if (requestDTO.fromUserId() <= 0 || requestDTO.receiverId() <= 0) {
                return ResponseEntity.badRequest().body("Invalid user IDs");
            }

            // Get the sender's email
            String fromUserEmail = userService.getEmailById(requestDTO.fromUserId());
            System.out.println(fromUserEmail);
            if (fromUserEmail == null) {
                return ResponseEntity.badRequest().body("Sender not found");
            }

            // Get the receiver's email
            String toUserEmail = userService.getEmailById(requestDTO.receiverId());
            System.out.println(toUserEmail);
            if (toUserEmail == null) {
                return ResponseEntity.badRequest().body("Receiver not found");
            }

            // Prevent self-connections
            if (fromUserEmail.equals(toUserEmail)) {
                return ResponseEntity.badRequest().body("Cannot send connection request to yourself");
            }

            // Check if a connection request already exists
            if (connectionRequestRepository.existsByFromUserAndToUser(fromUserEmail, toUserEmail)) {
                return ResponseEntity.badRequest().body("Connection request already exists");
            }

            // Create and save the connection request
            ConnectionRequest request = new ConnectionRequest(fromUserEmail, toUserEmail);
            connectionRequestRepository.save(request);

            // Send email notification
            emailService.sendConnectionRequest(fromUserEmail, toUserEmail);

            return ResponseEntity.ok().body(Map.of(
                    "message", "Connection request sent successfully",
                    "status", "success"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Error sending connection request: " + e.getMessage(),
                    "status", "error"
            ));
        }
    }


    @GetMapping("/connected-users")
    public ResponseEntity<?> getConnectedUsers() {
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Set<Users> connectedUsers = requestService.getConnectedUsers(userEmail);

            return ResponseEntity.ok(connectedUsers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Error fetching connected users: " + e.getMessage(),
                    "status", "error"
            ));
        }
    }


    @PostMapping("/accept-request/{id}")
    public String acceptRequest(@PathVariable int id) {
        return requestService.acceptRequest(id);
    }

    @PostMapping("/reject-request/{id}")
    public String rejectRequest(@PathVariable int id) {
        return requestService.rejectRequest(id);
    }

    @GetMapping("/pending-requests/{userEmail}")
    public List<ConnectionRequest> getPendingRequests(@PathVariable String userEmail) {
        return requestService.getPendingRequests(userEmail);
    }

    @GetMapping("/user-email/{userId}")
    public String getUserEmail(@PathVariable int userId) {
        return userService.getEmailById(userId);
    }
}
