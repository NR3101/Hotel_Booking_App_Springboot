package com.nr3101.hotelbookingapp.service;

import com.nr3101.hotelbookingapp.dto.request.HotelRequestDto;
import com.nr3101.hotelbookingapp.dto.request.HotelUpdateRequestDto;
import com.nr3101.hotelbookingapp.dto.response.HotelDetailsResponseDto;
import com.nr3101.hotelbookingapp.dto.response.HotelResponseDto;

public interface HotelService {

    HotelResponseDto createHotel(HotelRequestDto hotelRequestDTO);

    HotelResponseDto getHotelById(Long id);

    HotelResponseDto updateHotel(Long id, HotelUpdateRequestDto hotelUpdateRequestDTO);

    void deleteHotel(Long id);

    void activateHotel(Long id);

    HotelDetailsResponseDto getHotelDetails(Long hotelId);
}
