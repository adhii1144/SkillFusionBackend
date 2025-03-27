package com.example.talenttrove.doa;

import com.example.talenttrove.dto.RequestStatus;
import com.example.talenttrove.model.ConnectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, Integer> {
    List<ConnectionRequest> findByToUserAndStatus(String toUser, RequestStatus status);
    boolean existsByFromUserAndToUser(String fromUser, String toUser);

    @Query("SELECT cr FROM ConnectionRequest cr WHERE " +
            "(cr.fromUser = :userEmail OR cr.toUser = :userEmail) AND " +
            "cr.status = :status")
    List<ConnectionRequest> findUserRequests(@Param("userEmail") String userEmail,
                                             @Param("status") RequestStatus status);
}
