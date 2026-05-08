package com.nr3101.hotelbookingapp.service.impl;

import com.nr3101.hotelbookingapp.advice.ResourceNotFoundException;
import com.nr3101.hotelbookingapp.dto.request.HotelRequestDto;
import com.nr3101.hotelbookingapp.dto.request.HotelUpdateRequestDto;
import com.nr3101.hotelbookingapp.dto.response.HotelDetailsResponseDto;
import com.nr3101.hotelbookingapp.dto.response.HotelResponseDto;
import com.nr3101.hotelbookingapp.dto.response.RoomResponseDto;
import com.nr3101.hotelbookingapp.entity.Hotel;
import com.nr3101.hotelbookingapp.repository.HotelRepository;
import com.nr3101.hotelbookingapp.service.HotelService;
import com.nr3101.hotelbookingapp.service.InventoryService;
import com.nr3101.hotelbookingapp.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final RoomService roomService;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;

    @Override
    public HotelResponseDto createHotel(HotelRequestDto hotelRequestDTO) {
        log.info("Creating hotel with name: {}", hotelRequestDTO.getName());
        Hotel hotel = modelMapper.map(hotelRequestDTO, Hotel.class);
        hotel.setActive(false); // Default to inactive until approved
        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Hotel created with ID: {}", savedHotel.getId());
        return modelMapper.map(savedHotel, HotelResponseDto.class);
    }

    @Override
    public HotelResponseDto getHotelById(Long id) {
        log.info("Fetching hotel with ID: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + id));
        return modelMapper.map(hotel, HotelResponseDto.class);
    }

    @Override
    public HotelResponseDto updateHotel(Long id, HotelUpdateRequestDto hotelUpdateRequestDTO) {
        log.info("Updating hotel with ID: {}", id);
        Hotel existingHotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + id));

        if (hotelUpdateRequestDTO.getName() != null) {
            existingHotel.setName(hotelUpdateRequestDTO.getName());
        }
        if (hotelUpdateRequestDTO.getCity() != null) {
            existingHotel.setCity(hotelUpdateRequestDTO.getCity());
        }
        if (hotelUpdateRequestDTO.getPhotos() != null) {
            existingHotel.setPhotos(hotelUpdateRequestDTO.getPhotos());
        }
        if (hotelUpdateRequestDTO.getAmenities() != null) {
            existingHotel.setAmenities(hotelUpdateRequestDTO.getAmenities());
        }
        if (hotelUpdateRequestDTO.getContactInfo() != null) {
            existingHotel.setContactInfo(hotelUpdateRequestDTO.getContactInfo());
        }
        if (hotelUpdateRequestDTO.getActive() != null) {
            existingHotel.setActive(hotelUpdateRequestDTO.getActive());
        }

        Hotel updatedHotel = hotelRepository.save(existingHotel);
        log.info("Hotel updated with ID: {}", updatedHotel.getId());
        return modelMapper.map(updatedHotel, HotelResponseDto.class);
    }

    @Override
    @Transactional
    public void deleteHotel(Long id) {
        log.info("Deleting hotel with ID: {}", id);
        Hotel existingHotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + id));

        // Delete all future inventories and rooms associated with the hotel before deleting the hotel itself
        for (var room : existingHotel.getRooms()) {
            inventoryService.deleteAllInventories(room);
            roomService.deleteRoom(existingHotel.getId(), room.getId());
        }

        hotelRepository.delete(existingHotel);
        log.info("Hotel deleted with ID: {}", id);
    }

    @Override
    @Transactional
    public void activateHotel(Long id) {
        log.info("Activating hotel with ID: {}", id);
        Hotel existingHotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + id));
        existingHotel.setActive(true);

        // Initialize inventory for all rooms of the hotel when it is activated
        existingHotel
                .getRooms()
                .forEach(inventoryService::initializeRoomForAYear);

        hotelRepository.save(existingHotel);
        log.info("Hotel activated with ID: {}", id);
    }

    @Override
    public HotelDetailsResponseDto getHotelDetails(Long hotelId) {
        log.info("Fetching hotel details for hotel ID: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));

        List<RoomResponseDto> rooms = hotel.getRooms()
                .stream()
                .map(room -> modelMapper.map(room, RoomResponseDto.class))
                .toList();

        return new HotelDetailsResponseDto(modelMapper.map(hotel, HotelResponseDto.class), rooms);
    }
}
