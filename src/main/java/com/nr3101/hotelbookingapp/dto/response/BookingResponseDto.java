package com.nr3101.hotelbookingapp.dto.response;

import com.nr3101.hotelbookingapp.entity.enums.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Schema(description = "Booking details")
public class BookingResponseDto {

    @Schema(description = "Booking ID")
    private Long id;

    @Schema(description = "Number of rooms booked")
    private Integer roomsCount;

    @Schema(description = "Current booking status")
    private BookingStatus status;

    @Schema(description = "Check-in date")
    private LocalDate checkInDate;

    @Schema(description = "Check-out date")
    private LocalDate checkOutDate;

    @Schema(description = "Guests associated with the booking")
    private Set<GuestResponseDto> guests;

    @Schema(description = "Booking creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Total booking amount")
    private BigDecimal amount;
}
