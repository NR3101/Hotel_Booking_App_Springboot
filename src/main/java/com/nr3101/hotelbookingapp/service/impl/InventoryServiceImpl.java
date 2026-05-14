package com.nr3101.hotelbookingapp.service.impl;

import com.nr3101.hotelbookingapp.advice.ResourceNotFoundException;
import com.nr3101.hotelbookingapp.dto.request.HotelSearchRequestDto;
import com.nr3101.hotelbookingapp.dto.request.UpdateInventoryRequestDto;
import com.nr3101.hotelbookingapp.dto.response.HotelPriceResponseDto;
import com.nr3101.hotelbookingapp.dto.response.InventoryResponseDto;
import com.nr3101.hotelbookingapp.entity.Inventory;
import com.nr3101.hotelbookingapp.entity.Room;
import com.nr3101.hotelbookingapp.entity.User;
import com.nr3101.hotelbookingapp.repository.HotelMinPriceRepository;
import com.nr3101.hotelbookingapp.repository.InventoryRepository;
import com.nr3101.hotelbookingapp.repository.RoomRepository;
import com.nr3101.hotelbookingapp.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.nr3101.hotelbookingapp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    @Override
    public void initializeRoomForAYear(Room room) {
        log.info("Initializing inventory for room: {}", room.getId());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
                    .city(room.getHotel().getCity())
                    .date(date)
                    .totalCount(room.getTotalCount())
                    .price(room.getBasePrice()) // Initial price is the base price of the room
                    .surgeFactor(BigDecimal.ONE) // Default surge factor
                    .closed(false) // Room Inventory is open by default
                    .build();

            inventoryRepository.save(inventory);
            log.debug("Initialized inventory for date: {} with price: {}", date, inventory.getPrice());
        }
    }

    @Override
    public void deleteAllInventories(Room room) {
        log.info("Deleting future inventories for room: {}", room.getId());
        inventoryRepository.deleteByRoom(room);
        log.info("Deleted future inventories for room: {}", room.getId());
    }

    /**
     * Updates future inventory records if room details (basePrice or totalCount)
     * have changed.
     * Only updates inventory from today onwards; past bookings are not affected.
     *
     * @param room            the updated room with new values
     * @param oldRoomSnapshot the old room state before update
     */
    @Override
    public void updateRoomInventory(Room room, Room oldRoomSnapshot) {
        log.info("Updating future inventory for room: {}", room.getId());
        LocalDate today = LocalDate.now();

        // Check if basePrice changed
        boolean basePriceChanged = !room.getBasePrice().equals(oldRoomSnapshot.getBasePrice());
        // Check if totalCount changed
        boolean totalCountChanged = !room.getTotalCount().equals(oldRoomSnapshot.getTotalCount());

        if (!basePriceChanged && !totalCountChanged) {
            log.debug("No inventory-related changes detected for room: {}", room.getId());
            return;
        }

        // Get all future inventory records (from today onwards) for this room
        var futureInventories = inventoryRepository.findByRoomAndDateGreaterThanEqual(room, today);

        for (Inventory inventory : futureInventories) {
            // Update price if basePrice changed (price = basePrice * surgeFactor)
            if (basePriceChanged) {
                inventory.setPrice(room.getBasePrice().multiply(inventory.getSurgeFactor()));
                log.debug("Updated price for inventory on {} to: {}", inventory.getDate(), inventory.getPrice());
            }

            // Update totalCount if it changed
            if (totalCountChanged) {
                inventory.setTotalCount(room.getTotalCount());
                log.debug("Updated totalCount for inventory on {} to: {}", inventory.getDate(),
                        inventory.getTotalCount());
            }

            inventoryRepository.save(inventory);
        }

        log.info("Updated {} future inventory records for room: {}", futureInventories.size(), room.getId());
    }

    /**
     * Updates future inventory records for a hotel if the city has changed.
     * This ensures denormalized city field in inventory matches the hotel's city.
     *
     * @param hotelId the hotel ID
     * @param newCity the new city name
     */
    @Override
    public void updateHotelInventoryCity(Long hotelId, String newCity) {
        log.info("Updating inventory city for hotel: {} to: {}", hotelId, newCity);
        LocalDate today = LocalDate.now();

        // Get all future inventory records for the hotel (from today onwards)
        var futureInventories = inventoryRepository.findByHotelAndDateGreaterThanEqual(hotelId, today);

        for (Inventory inventory : futureInventories) {
            if (!inventory.getCity().equals(newCity)) {
                inventory.setCity(newCity);
                inventoryRepository.save(inventory);
                log.debug("Updated city for inventory on {} to: {}", inventory.getDate(), newCity);
            }
        }

        log.info("Updated {} future inventory records for hotel: {}", futureInventories.size(), hotelId);
    }

    @Override
    public Page<HotelPriceResponseDto> searchHotels(HotelSearchRequestDto searchRequest) {
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());
        // Calculate the number of days in the search range (inclusive)
        // long dateCount = ChronoUnit.DAYS
        // .between(searchRequest.getStartDate(), searchRequest.getEndDate()) + 1; // +1
        // to include end date

        // Page<Hotel> hotelPage = inventoryRepository.findHotelsWithAvailableInventory(
        // searchRequest.getCity(),
        // searchRequest.getStartDate(),
        // searchRequest.getEndDate(),
        // searchRequest.getRoomsCount(),
        // dateCount,
        // pageable
        // );

        return hotelMinPriceRepository.findHotelsWithAvailableInventory(
                searchRequest.getCity(),
                searchRequest.getStartDate(),
                searchRequest.getEndDate(),
                pageable);
    }

    @Override
    @Transactional
    public List<InventoryResponseDto> getInventoryForRoom(Long roomId) {
        log.info("Fetching inventory for room with id: {}", roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));

        User currentUser = getCurrentUser();

        // Check if the current user is the owner of the hotel to which the room belongs
        if (!room.getHotel().getOwner().equals(currentUser)) {
            throw new AccessDeniedException("You are not authorized to view inventory of this room");
        }

        return inventoryRepository.findByRoomOrderByDate(room).stream()
                .map(inventory -> modelMapper.map(inventory, InventoryResponseDto.class))
                .toList();
    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDto requestDto) {
        log.info("Updating inventory for room with id: {}", roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));

        User currentUser = getCurrentUser();

        // Check if the current user is the owner of the hotel to which the room belongs
        if (!room.getHotel().getOwner().equals(currentUser)) {
            throw new AccessDeniedException("You are not authorized to update inventory of this room");
        }

        // Lock inventory records for the specified date range to prevent concurrent
        // updates
        inventoryRepository.findAndLockInventoriesForUpdate(
                roomId,
                requestDto.getStartDate(),
                requestDto.getEndDate());

        // Update inventory records for the specified date range
        inventoryRepository.updateInventory(
                roomId,
                requestDto.getStartDate(),
                requestDto.getEndDate(),
                requestDto.getSurgeFactor(),
                requestDto.getClosed());

        log.info("Updated inventory for room with id: {}", roomId);
    }
}
