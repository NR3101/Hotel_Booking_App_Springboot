package com.nr3101.hotelbookingapp.service;

import com.nr3101.hotelbookingapp.dto.request.BookingRequestDto;
import com.nr3101.hotelbookingapp.dto.request.GuestRequestDto;
import com.nr3101.hotelbookingapp.dto.response.BookingResponseDto;
import jakarta.validation.Valid;

import java.util.List;

public interface BookingService {
    BookingResponseDto initializeBooking(@Valid BookingRequestDto bookingRequest);

    BookingResponseDto addGuestsToBooking(Long bookingId, @Valid List<GuestRequestDto> guestRequests);
}
