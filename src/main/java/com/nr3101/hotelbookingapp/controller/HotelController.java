package com.nr3101.hotelbookingapp.controller;

import com.nr3101.hotelbookingapp.dto.request.HotelRequestDto;
import com.nr3101.hotelbookingapp.dto.request.HotelUpdateRequestDto;
import com.nr3101.hotelbookingapp.dto.response.HotelResponseDto;
import com.nr3101.hotelbookingapp.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelResponseDto> createHotel(@Valid @RequestBody HotelRequestDto hotelRequestDTO) {
        log.info("Received request to create hotel: {}", hotelRequestDTO.getName());
        HotelResponseDto createdHotel = hotelService.createHotel(hotelRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdHotel);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelResponseDto> getHotelById(@PathVariable Long hotelId) {
        log.info("Received request to fetch hotel with ID: {}", hotelId);
        HotelResponseDto hotel = hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(hotel);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelResponseDto> updateHotel(@PathVariable Long hotelId, @RequestBody HotelUpdateRequestDto hotelUpdateRequestDTO) {
        log.info("Received request to update hotel with ID: {}", hotelId);
        HotelResponseDto updatedHotel = hotelService.updateHotel(hotelId, hotelUpdateRequestDTO);
        return ResponseEntity.ok(updatedHotel);
    }

    @PatchMapping("/{hotelId}/activate")
    public ResponseEntity<Void> activateHotel(@PathVariable Long hotelId) {
        log.info("Received request to activate hotel with ID: {}", hotelId);
        hotelService.activateHotel(hotelId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long hotelId) {
        log.info("Received request to delete hotel with ID: {}", hotelId);
        hotelService.deleteHotel(hotelId);
        return ResponseEntity.noContent().build();
    }
}
