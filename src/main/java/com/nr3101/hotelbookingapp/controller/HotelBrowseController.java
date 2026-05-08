package com.nr3101.hotelbookingapp.controller;

import com.nr3101.hotelbookingapp.dto.request.HotelSearchRequestDto;
import com.nr3101.hotelbookingapp.dto.response.HotelDetailsResponseDto;
import com.nr3101.hotelbookingapp.dto.response.HotelResponseDto;
import com.nr3101.hotelbookingapp.service.HotelService;
import com.nr3101.hotelbookingapp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelResponseDto>> searchHotels(@RequestBody HotelSearchRequestDto hotelSearchRequestDto) {
        log.info("Received hotel search request: {}", hotelSearchRequestDto);
        Page<HotelResponseDto> hotelsPage = inventoryService.searchHotels(hotelSearchRequestDto);
        log.info("Found {} hotels matching search criteria", hotelsPage.getTotalElements());
        return ResponseEntity.ok(hotelsPage);
    }

    @GetMapping("/{hotelId}/details")
    public ResponseEntity<HotelDetailsResponseDto> getHotelDetails(@PathVariable Long hotelId) {
        log.info("Received request for hotel details with ID: {}", hotelId);
        return ResponseEntity.ok(hotelService.getHotelDetails(hotelId));
    }
}
