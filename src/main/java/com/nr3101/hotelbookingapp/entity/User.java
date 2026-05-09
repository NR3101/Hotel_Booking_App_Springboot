package com.nr3101.hotelbookingapp.entity;

import com.nr3101.hotelbookingapp.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "app_user") // "user" is a reserved keyword in many databases, so we use "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // It will be hashed before storing in the database

    private String name; // Optional

    @ElementCollection(fetch = FetchType.EAGER) // so that a new table is created to store the roles of the user
    @Enumerated(EnumType.STRING)
    private List<Role> role; // Role of the user (e.g., GUEST, HOTEL_MANAGER)

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
