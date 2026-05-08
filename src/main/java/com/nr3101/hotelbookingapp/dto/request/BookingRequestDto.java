package com.nr3101.hotelbookingapp.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDto {

    private Long hotelId; // ID of the hotel being booked
    private Long roomId; // ID of the room being booked
    private LocalDate checkInDate; // Check-in date for the booking
    private LocalDate checkOutDate; // Check-out date for the booking
    private Integer roomsCount; // Number of rooms being booked
}
