package com.nr3101.hotelbookingapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Request payload to initialize a new booking")
public class BookingRequestDto {

    @Schema(description = "ID of the hotel being booked", example = "1")
    private Long hotelId;

    @Schema(description = "ID of the room type being booked", example = "5")
    private Long roomId;

    @Schema(description = "Check-in date", example = "2026-06-01")
    private LocalDate checkInDate;

    @Schema(description = "Check-out date", example = "2026-06-05")
    private LocalDate checkOutDate;

    @Schema(description = "Number of rooms to book", example = "2")
    private Integer roomsCount;
}
