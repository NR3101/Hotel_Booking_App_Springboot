package com.nr3101.hotelbookingapp.service;

import com.nr3101.hotelbookingapp.dto.request.RoomRequestDto;
import com.nr3101.hotelbookingapp.dto.response.RoomResponseDto;

import java.util.List;

public interface RoomService {

    RoomResponseDto createRoom(Long hotelId, RoomRequestDto roomRequestDto);

    RoomResponseDto getRoomById(Long hotelId, Long roomId);

    List<RoomResponseDto> getAllRoomsOfHotel(Long hotelId);

    void deleteRoom(Long hotelId, Long roomId);

    RoomResponseDto updateRoom(Long hotelId, Long roomId, RoomRequestDto roomRequestDto);
}
