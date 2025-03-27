package com.example.talenttrove.service;

import com.example.talenttrove.doa.ConnectionRequestRepository;
import com.example.talenttrove.doa.Users_Repo;
import com.example.talenttrove.dto.RequestStatus;
import com.example.talenttrove.model.ConnectionRequest;
import com.example.talenttrove.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ConnectionRequestService {
    @Autowired
    private ConnectionRequestRepository requestRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private Users_Repo usersRepo;

    public String sendRequest(String fromUser, String toUser) {
        ConnectionRequest request = new ConnectionRequest();
        request.setFromUser(fromUser);
        request.setToUser(toUser);
        request.setStatus(RequestStatus.PENDING);

        requestRepository.save(request);
        emailService.sendConnectionRequest(fromUser, toUser);

        return "Connection request sent!";
    }

    public String acceptRequest(int requestId) {
        Optional<ConnectionRequest> requestOpt = requestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            ConnectionRequest request = requestOpt.get();
            request.setStatus(RequestStatus.ACCEPTED);
            requestRepository.save(request);

            // Add users to each other's connection lists
            Users fromUser = usersRepo.findByEmail(request.getFromUser()).orElse(null);
            Users toUser = usersRepo.findByEmail(request.getToUser()).orElse(null);

            if (fromUser != null && toUser != null) {
                fromUser.addConnection(toUser);
                toUser.addConnection(fromUser);
                usersRepo.save(fromUser);
                usersRepo.save(toUser);
            }

            emailService.sendStatusUpdate(request.getFromUser(), request.getToUser(), "accepted");
            return "Request accepted!";
        }
        return "Request not found!";
    }

    public String rejectRequest(int requestId) {
        Optional<ConnectionRequest> requestOpt = requestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            ConnectionRequest request = requestOpt.get();
            request.setStatus(RequestStatus.REJECTED);
            requestRepository.save(request);

            emailService.sendStatusUpdate(request.getFromUser(), request.getToUser(), "rejected");
            return "Request rejected!";
        }
        return "Request not found!";
    }

    public List<ConnectionRequest> getPendingRequests(String userEmail) {
        return requestRepository.findByToUserAndStatus(userEmail, RequestStatus.PENDING);
    }

    public Set<Users> getConnectedUsers(String userEmail) {
        Users user = usersRepo.findByEmail(userEmail).orElse(null);
        return user != null ? user.getConnectedUsers() : new HashSet<>();
    }
}
