package com.nr3101.hotelbookingapp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "Full hotel details with rooms")
public class HotelDetailsResponseDto {

    @Schema(description = "Hotel summary")
    private HotelResponseDto hotel;

    @Schema(description = "Rooms available in the hotel")
    private List<RoomResponseDto> rooms;
}
