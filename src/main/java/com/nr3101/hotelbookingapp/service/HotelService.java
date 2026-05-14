package com.nr3101.hotelbookingapp.service;

import com.nr3101.hotelbookingapp.dto.request.HotelRequestDto;
import com.nr3101.hotelbookingapp.dto.request.HotelUpdateRequestDto;
import com.nr3101.hotelbookingapp.dto.response.BookingResponseDto;
import com.nr3101.hotelbookingapp.dto.response.HotelDetailsResponseDto;
import com.nr3101.hotelbookingapp.dto.response.HotelReportResponseDto;
import com.nr3101.hotelbookingapp.dto.response.HotelResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface HotelService {

    HotelResponseDto createHotel(HotelRequestDto hotelRequestDTO);

    HotelResponseDto getHotelById(Long id);

    HotelResponseDto updateHotel(Long id, HotelUpdateRequestDto hotelUpdateRequestDTO);

    void deleteHotel(Long id);

    void activateHotel(Long id);

    HotelDetailsResponseDto getHotelDetails(Long hotelId);

    List<HotelResponseDto> getAllHotels();

    List<BookingResponseDto> getHotelBookings(Long hotelId);

    HotelReportResponseDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);
}
