package com.nr3101.hotelbookingapp.service.impl;

import com.nr3101.hotelbookingapp.advice.ResourceNotFoundException;
import com.nr3101.hotelbookingapp.dto.request.RoomRequestDto;
import com.nr3101.hotelbookingapp.dto.response.RoomResponseDto;
import com.nr3101.hotelbookingapp.entity.Hotel;
import com.nr3101.hotelbookingapp.entity.Room;
import com.nr3101.hotelbookingapp.entity.User;
import com.nr3101.hotelbookingapp.repository.HotelRepository;
import com.nr3101.hotelbookingapp.repository.RoomRepository;
import com.nr3101.hotelbookingapp.service.InventoryService;
import com.nr3101.hotelbookingapp.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.nr3101.hotelbookingapp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public RoomResponseDto createRoom(Long hotelId, RoomRequestDto roomRequestDto) {
        log.info("Creating room for hotelId: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));

        // Check if the current user is the owner of the hotel
        User currentUser = getCurrentUser();
        if (!hotel.getOwner().equals(currentUser)) {
            throw new AccessDeniedException("You are not authorized to add rooms to this hotel");
        }

        Room room = modelMapper.map(roomRequestDto, Room.class);
        room.setHotel(hotel);
        Room savedRoom = roomRepository.save(room);
        log.info("Room created with id: {}", savedRoom.getId());

        // Initialize inventory for the new room for the next year
        if (hotel.getActive()) {
            inventoryService.initializeRoomForAYear(savedRoom);
        }

        return modelMapper.map(savedRoom, RoomResponseDto.class);
    }

    @Override
    public RoomResponseDto getRoomById(Long hotelId, Long roomId) {
        log.info("Fetching room with id: {} for hotelId: {}", roomId, hotelId);
        Room room = roomRepository.findByIdAndHotelId(roomId, hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId + " for hotel id: " + hotelId));
        return modelMapper.map(room, RoomResponseDto.class);
    }

    @Override
    public List<RoomResponseDto> getAllRoomsOfHotel(Long hotelId) {
        log.info("Fetching all rooms for hotelId: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));

        // Check if the current user is the owner of the hotel
        User currentUser = getCurrentUser();
        if (!hotel.getOwner().equals(currentUser)) {
            throw new AccessDeniedException("You are not authorized to view rooms of this hotel");
        }

        return hotel.getRooms().stream()
                .map(room -> modelMapper.map(room, RoomResponseDto.class))
                .toList();
    }

    @Override
    @Transactional
    public void deleteRoom(Long hotelId, Long roomId) {
        log.info("Deleting room with id: {} for hotelId: {}", roomId, hotelId);
        Room room = roomRepository.findByIdAndHotelId(roomId, hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId + " for hotel id: " + hotelId));

        // Check if the current user is the owner of the hotel
        User currentUser = getCurrentUser();
        if (!room.getHotel().getOwner().equals(currentUser)) {
            throw new AccessDeniedException("You are not authorized to delete rooms of this hotel");
        }

        // Delete future inventories for the deleted room
        inventoryService.deleteAllInventories(room);

        roomRepository.delete(room);
        log.info("Room deleted with id: {} for hotelId: {}", roomId, hotelId);
    }

    @Override
    @Transactional
    public RoomResponseDto updateRoom(Long hotelId, Long roomId, RoomRequestDto roomRequestDto) {
        log.info("Updating room with id: {} for hotelId: {}", roomId, hotelId);
        Room room = roomRepository.findByIdAndHotelId(roomId, hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId + " for hotel id: " + hotelId));

        // Check if the current user is the owner of the hotel
        User currentUser = getCurrentUser();
        if (!room.getHotel().getOwner().equals(currentUser)) {
            throw new AccessDeniedException("You are not authorized to update rooms of this hotel");
        }

        // Snapshot old room state before updating (needed for inventory sync)
        Room oldRoomSnapshot = new Room();
        oldRoomSnapshot.setId(room.getId());
        oldRoomSnapshot.setBasePrice(room.getBasePrice());
        oldRoomSnapshot.setTotalCount(room.getTotalCount());

        // Update room details
        if (roomRequestDto.getType() != null) {
            room.setType(roomRequestDto.getType());
        }
        if (roomRequestDto.getBasePrice() != null) {
            room.setBasePrice(roomRequestDto.getBasePrice());
        }
        if (roomRequestDto.getPhotos() != null) {
            room.setPhotos(roomRequestDto.getPhotos());
        }
        if (roomRequestDto.getAmenities() != null) {
            room.setAmenities(roomRequestDto.getAmenities());
        }
        if (roomRequestDto.getTotalCount() != null) {
            room.setTotalCount(roomRequestDto.getTotalCount());
        }
        if (roomRequestDto.getCapacity() != null) {
            room.setCapacity(roomRequestDto.getCapacity());
        }

        Room updatedRoom = roomRepository.save(room);

        // Sync inventory if basePrice or totalCount changed
        inventoryService.updateRoomInventory(updatedRoom, oldRoomSnapshot);

        log.info("Room updated with id: {} for hotelId: {}", roomId, hotelId);
        return modelMapper.map(updatedRoom, RoomResponseDto.class);
    }
}
