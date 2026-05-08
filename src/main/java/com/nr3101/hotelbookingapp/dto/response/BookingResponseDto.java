package com.nr3101.hotelbookingapp.dto.response;

import com.nr3101.hotelbookingapp.entity.role.BookingStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingResponseDto {

    private Long id;
    private Integer roomsCount; // Number of rooms booked
    private BookingStatus status; // Status of the booking (e.g., CONFIRMED, CANCELLED)
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Set<GuestResponseDto> guests; // Set of guests associated with the booking
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
