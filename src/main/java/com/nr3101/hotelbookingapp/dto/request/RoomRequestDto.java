package com.nr3101.hotelbookingapp.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomRequestDto {

    @NotBlank(message = "Room type is required")
    private String type; // e.g., "Single", "Double", "Suite"

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
    private BigDecimal basePrice; // e.g., 100.00

    private String[] photos;
    private String[] amenities;

    @NotNull(message = "Total count is required")
    private Integer totalCount; // Total number of rooms of this type available in the hotel

    @NotNull(message = "Capacity is required")
    private Integer capacity; // Number of guests the room can accommodate
}
