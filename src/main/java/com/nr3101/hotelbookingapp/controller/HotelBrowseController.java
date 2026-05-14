package com.nr3101.hotelbookingapp.controller;

import com.nr3101.hotelbookingapp.dto.request.HotelSearchRequestDto;
import com.nr3101.hotelbookingapp.dto.response.HotelDetailsResponseDto;
import com.nr3101.hotelbookingapp.dto.response.HotelPriceResponseDto;
import com.nr3101.hotelbookingapp.service.HotelService;
import com.nr3101.hotelbookingapp.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Hotel Browsing", description = "Public endpoints for searching and viewing hotel details")
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @Operation(summary = "Search hotels", description = "Searches for available hotels by city, dates, and room count with pricing")
    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceResponseDto>> searchHotels(@RequestBody HotelSearchRequestDto hotelSearchRequestDto) {
        log.info("Received hotel search request: {}", hotelSearchRequestDto);
        Page<HotelPriceResponseDto> hotelsPage = inventoryService.searchHotels(hotelSearchRequestDto);
        log.info("Found {} hotels matching search criteria", hotelsPage.getTotalElements());
        return ResponseEntity.ok(hotelsPage);
    }

    @Operation(summary = "Get hotel details", description = "Returns full hotel details including all rooms for the given hotel")
    @GetMapping("/{hotelId}/details")
    public ResponseEntity<HotelDetailsResponseDto> getHotelDetails(@PathVariable Long hotelId) {
        log.info("Received request for hotel details with ID: {}", hotelId);
        return ResponseEntity.ok(hotelService.getHotelDetails(hotelId));
    }
}
