package com.nr3101.hotelbookingapp.controller;

import com.nr3101.hotelbookingapp.dto.request.BookingRequestDto;
import com.nr3101.hotelbookingapp.dto.request.GuestRequestDto;
import com.nr3101.hotelbookingapp.dto.response.BookingResponseDto;
import com.nr3101.hotelbookingapp.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingResponseDto> initializeBooking(
            @Valid @RequestBody BookingRequestDto bookingRequest
    ) {
        return ResponseEntity.ok(bookingService.initializeBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingResponseDto> addGuestsToBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody List<GuestRequestDto> guestRequests
    ) {
        return ResponseEntity.ok(bookingService.addGuestsToBooking(bookingId, guestRequests));
    }
}
