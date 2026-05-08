package com.nr3101.hotelbookingapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "inventory",
        // Unique constraint to prevent duplicate inventory entries for the same hotel, room, and date
        uniqueConstraints = @UniqueConstraint(
                name = "unique_hotel_room_date",
                columnNames = {"hotel_id", "room_id", "date"}
        )
)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel; // Many-to-One relationship with Hotel

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room; // Many-to-One relationship with Room

    @Column(nullable = false)
    private LocalDate date; // Date for which the inventory is tracked

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer bookedCount; // Number of rooms booked for this date

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer reservedCount; // Number of rooms reserved (e.g., in the process of booking but not yet confirmed) for this date

    @Column(nullable = false)
    private Integer totalCount; // Total number of rooms available for this date

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal surgeFactor; // Surge factor for dynamic pricing

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // Price for this date, calculated as room basePrice * surge factor

    @Column(nullable = false)
    private String city; // City for easier querying and filtering to avoid joins

    private Boolean closed; // Indicates if the room is closed for this date

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
