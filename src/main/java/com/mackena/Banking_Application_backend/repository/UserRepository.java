package com.mackena.Banking_Application_backend.repository;

import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.models.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Page<User> findAll(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.isEnabled = true")
    Page<User> findAllActiveUsers(Pageable pageable);

    @Query("SELECT SUM(a.balance) FROM User u JOIN u.accounts a WHERE u.id = :userId AND a.isActive = true")
    BigDecimal getTotalBalanceByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(a) FROM User u JOIN u.accounts a WHERE u.id = :userId AND a.isActive = true")
    int getActiveAccountCountByUserId(@Param("userId") Long userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.accounts WHERE u.id = :id")
    Optional<User> findByIdWithAccounts(@Param("id") Long id);
}