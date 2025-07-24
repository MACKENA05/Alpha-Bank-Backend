package com.mackena.Banking_Application_backend.repository;

import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.models.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

//    Authentication
    Optional<User> findByEmail(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

//    User Management

    List<User> findByRole(UserRole role);

    List<User> findByIsActive(Boolean isActive);

    List<User> findByRole(UserRole role, Pageable pageable);

//    Search
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameOrEmailContaining(@Param("name") String name, @Param("email") String email);

    // Statistics queries
    long countByRole(UserRole role);

    long countByIsActive(Boolean isActive);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :date")
    long countUsersCreatedAfter(@Param("date") LocalDateTime date);

    // Admin queries
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN' AND u.isActive = true")
    List<User> findActiveAdmins();

}
