package com.mackena.Banking_Application_backend.models.entity;


import com.mackena.Banking_Application_backend.models.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name= "idx_user_phone", columnList = "phone_number")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"accounts"})
@ToString(exclude = {"accounts"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false,length = 255)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Account> accounts = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

//    utils methods

    public void addAccount(Account account) {
        accounts.add(account);
        account.setUser(this);
    }
    public void removeAccount(Account account) {
        accounts.remove(account);
        account.setUser(null);
    }
    public boolean isAdmin(){
        return UserRole.ADMIN.equals(this.role);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }






}
