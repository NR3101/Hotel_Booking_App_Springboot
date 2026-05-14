package com.nr3101.hotelbookingapp.controller;

import com.nr3101.hotelbookingapp.dto.request.RoomRequestDto;
import com.nr3101.hotelbookingapp.dto.response.RoomResponseDto;
import com.nr3101.hotelbookingapp.service.RoomService;
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
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Room Management (Admin)", description = "Admin endpoints for managing rooms within a hotel")
@SecurityRequirement(name = "bearerAuth")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "Create a room", description = "Adds a new room type to the specified hotel")
    @PostMapping
    public ResponseEntity<RoomResponseDto> createRoom(
            @PathVariable Long hotelId,
            @Valid @RequestBody RoomRequestDto roomRequestDto) {
        log.info("Received request to create room for hotelId: {}", hotelId);
        RoomResponseDto createdRoom = roomService.createRoom(hotelId, roomRequestDto);
        log.info("Room created successfully with id: {}", createdRoom.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    @Operation(summary = "Get room by ID", description = "Returns details of a specific room in the hotel")
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponseDto> getRoomById(@PathVariable Long hotelId, @PathVariable Long roomId) {
        log.info("Received request to get room with id: {} for hotelId: {}", roomId, hotelId);
        RoomResponseDto roomResponseDto = roomService.getRoomById(hotelId, roomId);
        log.info("Room fetched successfully with id: {} for hotelId: {}", roomId, hotelId);
        return ResponseEntity.ok(roomResponseDto);
    }

    @Operation(summary = "Get all rooms", description = "Returns all rooms for the specified hotel")
    @GetMapping
    public ResponseEntity<List<RoomResponseDto>> getAllRoomsOfHotel(@PathVariable Long hotelId) {
        log.info("Received request to get all rooms for hotelId: {}", hotelId);
        List<RoomResponseDto> response = roomService.getAllRoomsOfHotel(hotelId);
        log.info("Rooms fetched successfully for hotelId: {}", hotelId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a room", description = "Removes a room type from the hotel")
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long hotelId, @PathVariable Long roomId) {
        log.info("Received request to delete room with id: {} for hotelId: {}", roomId, hotelId);
        roomService.deleteRoom(hotelId, roomId);
        log.info("Room deleted successfully with id: {} for hotelId: {}", roomId, hotelId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a room", description = "Updates the details of an existing room")
    @PutMapping("/{roomId}")
    public ResponseEntity<RoomResponseDto> updateRoom(
            @PathVariable Long hotelId,
            @PathVariable Long roomId,
            @RequestBody RoomRequestDto roomRequestDto) {
        log.info("Received request to update room with id: {} for hotelId: {}", roomId, hotelId);
        RoomResponseDto updatedRoom = roomService.updateRoom(hotelId, roomId, roomRequestDto);
        log.info("Room updated successfully with id: {} for hotelId: {}", roomId, hotelId);
        return ResponseEntity.ok(updatedRoom);
    }
}
