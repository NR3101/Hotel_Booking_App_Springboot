package com.nr3101.hotelbookingapp.entity;

import com.nr3101.hotelbookingapp.entity.role.PaymentStatus;
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
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String transactionId; // Unique identifier for the payment transaction

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status; // Enum to represent payment status (e.g., PENDING, COMPLETED, FAILED)

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // Amount paid

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
