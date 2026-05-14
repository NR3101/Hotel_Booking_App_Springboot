package com.nr3101.hotelbookingapp.service;

import com.nr3101.hotelbookingapp.dto.request.GuestRequestDto;
import com.nr3101.hotelbookingapp.dto.response.GuestResponseDto;
import jakarta.validation.Valid;

import java.util.List;

public interface GuestService {

    List<GuestResponseDto> getAllGuests();

    GuestResponseDto addNewGuest(@Valid GuestRequestDto guestRequestDto);

    void updateGuest(Long guestId, @Valid GuestRequestDto guestRequestDto);

    void deleteGuest(Long guestId);
}
