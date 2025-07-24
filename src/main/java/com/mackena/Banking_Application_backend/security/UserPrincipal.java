package com.mackena.Banking_Application_backend.security;

import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.models.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.enabled;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserRole role;
    private boolean isEnabled;

    public static UserPrincipal create(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.isActive()
        );
    }
    //specifies user roles for authorization
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return enabled; }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
