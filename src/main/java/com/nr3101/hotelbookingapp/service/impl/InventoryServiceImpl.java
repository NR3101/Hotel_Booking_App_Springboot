package com.nr3101.hotelbookingapp.service.impl;

import com.nr3101.hotelbookingapp.dto.request.HotelSearchRequestDto;
import com.nr3101.hotelbookingapp.dto.response.HotelResponseDto;
import com.nr3101.hotelbookingapp.entity.Hotel;
import com.nr3101.hotelbookingapp.entity.Inventory;
import com.nr3101.hotelbookingapp.entity.Room;
import com.nr3101.hotelbookingapp.repository.InventoryRepository;
import com.nr3101.hotelbookingapp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
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

    @Override
    public Page<HotelResponseDto> searchHotels(HotelSearchRequestDto searchRequest) {
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());
        // Calculate the number of days in the search range (inclusive)
        long dateCount = ChronoUnit.DAYS
                .between(searchRequest.getStartDate(), searchRequest.getEndDate()) + 1; // +1 to include end date

        Page<Hotel> hotelPage = inventoryRepository.findHotelsWithAvailableInventory(
                searchRequest.getCity(),
                searchRequest.getStartDate(),
                searchRequest.getEndDate(),
                searchRequest.getRoomsCount(),
                dateCount,
                pageable
        );

        return hotelPage.map((element) -> modelMapper.map(element, HotelResponseDto.class));
    }
}
