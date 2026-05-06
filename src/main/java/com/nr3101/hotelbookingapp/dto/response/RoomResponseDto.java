package com.nr3101.hotelbookingapp.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomResponseDto {

    private Long id;
    private String type; // e.g., "Single", "Double", "Suite"
    private BigDecimal basePrice; // e.g., 100.00
    private String[] photos;
    private String[] amenities;
    private Integer totalCount; // Total number of rooms of this type available in the hotel
    private Integer capacity; // Number of guests the room can accommodate
}
