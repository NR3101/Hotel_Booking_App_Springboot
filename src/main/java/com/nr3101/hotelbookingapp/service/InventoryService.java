package com.nr3101.hotelbookingapp.service;

import com.nr3101.hotelbookingapp.dto.request.HotelSearchRequestDto;
import com.nr3101.hotelbookingapp.dto.response.HotelResponseDto;
import com.nr3101.hotelbookingapp.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelResponseDto> searchHotels(HotelSearchRequestDto hotelSearchRequestDto);
}
