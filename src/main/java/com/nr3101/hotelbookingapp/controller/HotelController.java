package com.nr3101.hotelbookingapp.controller;

import com.nr3101.hotelbookingapp.dto.request.HotelRequestDto;
import com.nr3101.hotelbookingapp.dto.request.HotelUpdateRequestDto;
import com.nr3101.hotelbookingapp.dto.response.BookingResponseDto;
import com.nr3101.hotelbookingapp.dto.response.HotelReportResponseDto;
import com.nr3101.hotelbookingapp.dto.response.HotelResponseDto;
import com.nr3101.hotelbookingapp.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Hotel Management (Admin)", description = "Admin endpoints for managing hotels")
@SecurityRequirement(name = "bearerAuth")
public class HotelController {

    private final HotelService hotelService;

    @Operation(summary = "Create a new hotel", description = "Registers a new hotel in the system")
    @PostMapping
    public ResponseEntity<HotelResponseDto> createHotel(@Valid @RequestBody HotelRequestDto hotelRequestDTO) {
        log.info("Received request to create hotel: {}", hotelRequestDTO.getName());
        HotelResponseDto createdHotel = hotelService.createHotel(hotelRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdHotel);
    }

    @Operation(summary = "Get all hotels", description = "Returns all hotels owned by the current hotel manager")
    @GetMapping
    public ResponseEntity<List<HotelResponseDto>> getAllHotels() {
        log.info("Received request to fetch all hotels");
        List<HotelResponseDto> hotels = hotelService.getAllHotels();
        return ResponseEntity.ok(hotels);
    }

    @Operation(summary = "Get hotel by ID", description = "Returns details of a specific hotel")
    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelResponseDto> getHotelById(@PathVariable Long hotelId) {
        log.info("Received request to fetch hotel with ID: {}", hotelId);
        HotelResponseDto hotel = hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(hotel);
    }

    @Operation(summary = "Update a hotel", description = "Updates the details of an existing hotel")
    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelResponseDto> updateHotel(@PathVariable Long hotelId, @RequestBody HotelUpdateRequestDto hotelUpdateRequestDTO) {
        log.info("Received request to update hotel with ID: {}", hotelId);
        HotelResponseDto updatedHotel = hotelService.updateHotel(hotelId, hotelUpdateRequestDTO);
        return ResponseEntity.ok(updatedHotel);
    }

    @Operation(summary = "Activate a hotel", description = "Activates an inactive hotel and generates inventory for its rooms")
    @PatchMapping("/{hotelId}/activate")
    public ResponseEntity<Void> activateHotel(@PathVariable Long hotelId) {
        log.info("Received request to activate hotel with ID: {}", hotelId);
        hotelService.activateHotel(hotelId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a hotel", description = "Permanently deletes a hotel and all associated data")
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long hotelId) {
        log.info("Received request to delete hotel with ID: {}", hotelId);
        hotelService.deleteHotel(hotelId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get hotel bookings", description = "Returns all bookings for the specified hotel")
    @GetMapping("/{hotelId}/bookings")
    public ResponseEntity<List<BookingResponseDto>> getHotelBookings(@PathVariable Long hotelId) {
        log.info("Received request to fetch bookings for hotel with ID: {}", hotelId);
        List<BookingResponseDto> bookings = hotelService.getHotelBookings(hotelId);
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Get hotel report", description = "Returns booking and revenue report for the hotel, optionally filtered by date range")
    @GetMapping("/{hotelId}/reports")
    public ResponseEntity<HotelReportResponseDto> getHotelReport(
            @PathVariable Long hotelId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        log.info("Received request to fetch report for hotel with ID: {} from {} to {}", hotelId, startDate, endDate);
        HotelReportResponseDto report = hotelService.getHotelReport(hotelId, startDate, endDate);
        return ResponseEntity.ok(report);
    }
}
