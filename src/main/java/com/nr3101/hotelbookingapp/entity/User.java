package com.nr3101.hotelbookingapp.entity;

import com.nr3101.hotelbookingapp.entity.enums.Gender;
import com.nr3101.hotelbookingapp.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
// "user" is a reserved keyword in many databases, so we use "app_user")
@Table(name = "app_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // It will be hashed before storing in the database

    private String name; // Name of the user (e.g., John Doe)

    @Column(unique = true, length = 10)
    private String phoneNumber; // Phone number of the user

    private LocalDate dateOfBirth; // Date of birth of the user

    @Enumerated(EnumType.STRING)
    private Gender gender; // Gender of the user

    @ElementCollection(fetch = FetchType.EAGER) // so that a new table is created to store the roles of the user
    @Enumerated(EnumType.STRING)
    private Set<Role> roles; // Role of the user (e.g., GUEST, HOTEL_MANAGER)

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /* Equals and hashCode based on id */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /* UserDetails interface methods */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                // Spring Security expects roles to be prefixed with "ROLE_"
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return email;
    }
}
