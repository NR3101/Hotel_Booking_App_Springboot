package com.nr3101.hotelbookingapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request payload to create or update a room")
public class RoomRequestDto {

    @NotBlank(message = "Room type is required")
    @Schema(description = "Room type", example = "Deluxe Suite")
    private String type;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
    @Schema(description = "Base price per night", example = "150.00")
    private BigDecimal basePrice;

    @Schema(description = "Array of photo URLs")
    private String[] photos;

    @Schema(description = "Array of amenity names")
    private String[] amenities;

    @NotNull(message = "Total count is required")
    @Schema(description = "Total number of rooms of this type", example = "10")
    private Integer totalCount;

    @NotNull(message = "Capacity is required")
    @Schema(description = "Max guest capacity per room", example = "2")
    private Integer capacity;
}
