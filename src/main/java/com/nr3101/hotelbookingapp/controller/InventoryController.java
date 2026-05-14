package com.nr3101.hotelbookingapp.controller;

import com.nr3101.hotelbookingapp.dto.request.UpdateInventoryRequestDto;
import com.nr3101.hotelbookingapp.dto.response.InventoryResponseDto;
import com.nr3101.hotelbookingapp.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventory Management (Admin)", description = "Admin endpoints for viewing and updating room inventory")
@SecurityRequirement(name = "bearerAuth")
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Get inventory for a room", description = "Returns daily inventory records for the specified room")
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<InventoryResponseDto>> getInventoryForRoom(@PathVariable Long roomId) {
        log.info("Received request to fetch inventory for room with id: {}", roomId);
        List<InventoryResponseDto> response = inventoryService.getInventoryForRoom(roomId);
        log.info("Inventory fetched successfully for room with id: {}", roomId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update room inventory", description = "Updates surge factor and/or availability for a room within a date range")
    @PatchMapping("/rooms/{roomId}")
    public ResponseEntity<Void> updateRoomInventory(
            @PathVariable Long roomId,
            @RequestBody UpdateInventoryRequestDto updateInventoryRequestDto) {
        log.info("Received request to update inventory for room with id: {}", roomId);
        inventoryService.updateInventory(roomId, updateInventoryRequestDto);
        log.info("Inventory updated successfully for room with id: {}", roomId);
        return ResponseEntity.noContent().build();

    }
}
