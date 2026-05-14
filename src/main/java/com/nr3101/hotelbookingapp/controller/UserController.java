package com.nr3101.hotelbookingapp.controller;

import com.nr3101.hotelbookingapp.dto.request.GuestRequestDto;
import com.nr3101.hotelbookingapp.dto.request.ProfileUpdateRequestDto;
import com.nr3101.hotelbookingapp.dto.response.BookingResponseDto;
import com.nr3101.hotelbookingapp.dto.response.GuestResponseDto;
import com.nr3101.hotelbookingapp.dto.response.UserResponseDto;
import com.nr3101.hotelbookingapp.service.BookingService;
import com.nr3101.hotelbookingapp.service.GuestService;
import com.nr3101.hotelbookingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "Manage user profile, bookings, and saved guests")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;
    private final GuestService guestService;

    @Operation(summary = "Get profile")
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> getProfile() {
        log.info("Received request to get user profile for the current user");
        UserResponseDto userProfile = userService.getUserProfile();
        return ResponseEntity.ok(userProfile);
    }

    @Operation(summary = "Update profile")
    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(@Valid @RequestBody ProfileUpdateRequestDto profileUpdateRequestDto) {
        log.info("Received request to update user profile: {}", profileUpdateRequestDto);
        userService.updateUserProfile(profileUpdateRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get my bookings")
    @GetMapping("/myBookings")
    public ResponseEntity<List<BookingResponseDto>> getMyBookings() {
        log.info("Received request to get bookings for the current user");
        List<BookingResponseDto> bookings = bookingService.getBookingsForCurrentUser();
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Get all saved guests")
    @GetMapping("/guests")
    public ResponseEntity<List<GuestResponseDto>> getAllGuests() {
        log.info("Received request to get all guests for the current user");
        List<GuestResponseDto> guests = guestService.getAllGuests();
        return ResponseEntity.ok(guests);
    }

    @Operation(summary = "Add a new guest")
    @PostMapping("/guests")
    public ResponseEntity<GuestResponseDto> addNewGuest(@Valid @RequestBody GuestRequestDto guestRequestDto) {
        log.info("Received request to add new guest: {}", guestRequestDto);
        GuestResponseDto newGuest = guestService.addNewGuest(guestRequestDto);
        return new ResponseEntity<>(newGuest, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a guest")
    @PutMapping("/guests/{guestId}")
    public ResponseEntity<Void> updateGuest(@PathVariable Long guestId, @Valid @RequestBody GuestRequestDto guestRequestDto) {
        log.info("Received request to update guest with ID: {} with data: {}", guestId, guestRequestDto);
        guestService.updateGuest(guestId, guestRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a guest")
    @DeleteMapping("/guests/{guestId}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long guestId) {
        log.info("Received request to delete guest with ID: {}", guestId);
        guestService.deleteGuest(guestId);
        return ResponseEntity.noContent().build();
    }
}
