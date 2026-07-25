package com.jpcode.screentrack.user;

import com.jpcode.screentrack.exception.BusinessRuleException;
import com.jpcode.screentrack.user.dto.UserRegisterRequestDto;
import com.jpcode.screentrack.user.dto.UserUpdateRequestDto;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="usuarios")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(unique = true, nullable = false)
    private String username;
    private String bio;

    public String getUserName() {
        return username;
    }


    private String tokenVerification;
    private LocalDateTime creationDate;
    private LocalDateTime expirationDateToken;
    @Column(nullable = false)
    private boolean active;
    @Column(nullable = false)
    private boolean verified;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Deprecated
    public User() {
    }

    public User(UserRegisterRequestDto dto, String encryptedPassword) {
        this.name = dto.name();
        this.email = dto.email();
        this.password = encryptedPassword;
        this.username = dto.username();
        this.bio = dto.bio();
        this.tokenVerification = UUID.randomUUID().toString();
        this.creationDate = LocalDateTime.now();
        this.expirationDateToken = LocalDateTime.now().plusMinutes(30);
        this.active = false;
        this.verified = false;
        this.role = Role.USER;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
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

    public String getBio() {
        return bio;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getTokenVerification() {
        return tokenVerification;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getExpirationDateToken() {
        return expirationDateToken;
    }

    public Boolean getActive() {
        return active;
    }

    public Role getRole() {
        return role;
    }

    public void desativate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public void verify() {
        if (expirationDateToken.isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Link de verificação expirou!");
        }
        this.active = true;
        this.verified = true;
        this.tokenVerification = null;
        this.expirationDateToken = null;
    }

    public User updateData(UserUpdateRequestDto dto) {
        if (dto.username() !=  null) {
            this.username = dto.username();
        }
        if (dto.bio() !=  null) {
            this.bio = dto.bio();
        }
        return this;
    }

    public void genNewVerifiedToken() {
        this.tokenVerification = UUID.randomUUID().toString();
        this.expirationDateToken = LocalDateTime.now().plusMinutes(30);
    }

    public void updatePassword(String encryptedPassword) {
        this.password = encryptedPassword;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    public void reactivate() {
        this.active = true;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isVerified() {
        return verified;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof User user)) {
            return false;
        }

        return id != null && id.equals(user.id);
    }


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
