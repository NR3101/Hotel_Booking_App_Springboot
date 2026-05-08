package com.nr3101.hotelbookingapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HotelDetailsResponseDto {
    private HotelResponseDto hotel;
    private List<RoomResponseDto> rooms;
}
