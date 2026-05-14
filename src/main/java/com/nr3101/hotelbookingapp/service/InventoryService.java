package com.nr3101.hotelbookingapp.service;

import com.nr3101.hotelbookingapp.dto.request.HotelSearchRequestDto;
import com.nr3101.hotelbookingapp.dto.request.UpdateInventoryRequestDto;
import com.nr3101.hotelbookingapp.dto.response.HotelPriceResponseDto;
import com.nr3101.hotelbookingapp.dto.response.InventoryResponseDto;
import com.nr3101.hotelbookingapp.entity.Room;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

    void updateRoomInventory(Room room, Room oldRoomSnapshot);

    void updateHotelInventoryCity(Long hotelId, String newCity);

    Page<HotelPriceResponseDto> searchHotels(HotelSearchRequestDto hotelSearchRequestDto);

    List<InventoryResponseDto> getInventoryForRoom(Long roomId);

    void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);
}
