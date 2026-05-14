package com.nr3101.hotelbookingapp.controller;

import com.nr3101.hotelbookingapp.dto.request.BookingRequestDto;
import com.nr3101.hotelbookingapp.dto.request.GuestRequestDto;
import com.nr3101.hotelbookingapp.dto.response.BookingResponseDto;
import com.nr3101.hotelbookingapp.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bookings", description = "Create, manage, and track hotel bookings")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Initialize a booking", description = "Reserves rooms and creates a new booking in RESERVED status")
    @PostMapping("/init")
    public ResponseEntity<BookingResponseDto> initializeBooking(
            @Valid @RequestBody BookingRequestDto bookingRequest
    ) {
        return ResponseEntity.ok(bookingService.initializeBooking(bookingRequest));
    }

    @Operation(summary = "Add guests to a booking", description = "Associates guest details with an existing booking")
    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingResponseDto> addGuestsToBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody List<GuestRequestDto> guestRequests
    ) {
        return ResponseEntity.ok(bookingService.addGuestsToBooking(bookingId, guestRequests));
    }

    @Operation(summary = "Initiate payment", description = "Creates a Stripe checkout session and returns the payment URL")
    @PostMapping("/{bookingId}/payment")
    public ResponseEntity<Map<String, String>> initiatePayment(
            @PathVariable Long bookingId
    ) {
        String sessionUrl = bookingService.initiatePayment(bookingId);
        return ResponseEntity.ok(Map.of("sessionUrl", sessionUrl));
    }

    @Operation(summary = "Cancel a booking", description = "Cancels the booking and releases the reserved inventory")
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long bookingId
    ) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get booking status", description = "Returns the current status of the specified booking")
    @GetMapping("/{bookingId}/status")
    public ResponseEntity<Map<String, String>> getBookingStatus(
            @PathVariable Long bookingId
    ) {
        String status = bookingService.getBookingStatus(bookingId);
        return ResponseEntity.ok(Map.of("bookingStatus", status));
    }
}
