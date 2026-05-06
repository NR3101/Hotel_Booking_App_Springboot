package com.nr3101.hotelbookingapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel; // Many-to-One relationship with Hotel

    @Column(nullable = false)
    private String type; // e.g., "Single", "Double", "Suite"

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice; // e.g., 100.00

    @Column(columnDefinition = "TEXT[]")
    private String[] photos;

    @Column(columnDefinition = "TEXT[]")
    private String[] amenities;

    @Column(nullable = false)
    private Integer totalCount; // Total number of rooms of this type available in the hotel

    @Column(nullable = false)
    private Integer capacity; // Number of guests the room can accommodate

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
