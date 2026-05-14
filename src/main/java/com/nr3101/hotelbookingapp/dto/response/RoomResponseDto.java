package com.nr3101.hotelbookingapp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Room details")
public class RoomResponseDto {

    @Schema(description = "Room ID")
    private Long id;

    @Schema(description = "Room type (e.g. Single, Double, Suite)")
    private String type;

    @Schema(description = "Base price per night")
    private BigDecimal basePrice;

    @Schema(description = "Photo URLs")
    private String[] photos;

    @Schema(description = "Amenities")
    private String[] amenities;

    @Schema(description = "Total rooms of this type")
    private Integer totalCount;

    @Schema(description = "Max guest capacity")
    private Integer capacity;
}
