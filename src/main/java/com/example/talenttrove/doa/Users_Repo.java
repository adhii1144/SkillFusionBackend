package com.example.talenttrove.doa;

import com.example.talenttrove.model.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface Users_Repo extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    @Query("SELECT u FROM Users u WHERE u.username = :name")
    Optional<Users> findByName(@Param("name") String name);

    @Query("SELECT u FROM Users u WHERE u.username != :username")
    List<Users> findAllExceptCurrentUser(@Param("username") String username);


    @Query("SELECT u FROM Users u " +
            "WHERE (:query IS NULL OR u.username LIKE %:query%) " +
            "AND (:bio IS NULL OR u.bio LIKE %:bio%) " +
            "AND (:address IS NULL OR u.address LIKE %:address%) " +
            "AND u.email != :email")
    List<Users> searchUsers(@Param("query") String query,
                            @Param("bio") String bio,
                            @Param("address") String address,
                            @Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.password = ?2 WHERE u.email = ?1")
    void updatePassword(String email, String password);
}


