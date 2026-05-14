package com.nr3101.hotelbookingapp.service.impl;

import com.nr3101.hotelbookingapp.advice.ResourceNotFoundException;
import com.nr3101.hotelbookingapp.dto.request.HotelRequestDto;
import com.nr3101.hotelbookingapp.dto.request.HotelUpdateRequestDto;
import com.nr3101.hotelbookingapp.dto.response.*;
import com.nr3101.hotelbookingapp.entity.Booking;
import com.nr3101.hotelbookingapp.entity.Hotel;
import com.nr3101.hotelbookingapp.entity.User;
import com.nr3101.hotelbookingapp.entity.enums.BookingStatus;
import com.nr3101.hotelbookingapp.repository.BookingRepository;
import com.nr3101.hotelbookingapp.repository.HotelRepository;
import com.nr3101.hotelbookingapp.service.HotelService;
import com.nr3101.hotelbookingapp.service.InventoryService;
import com.nr3101.hotelbookingapp.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static com.nr3101.hotelbookingapp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final RoomService roomService;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;
    private final BookingRepository bookingRepository;

    @Override
    public HotelResponseDto createHotel(HotelRequestDto hotelRequestDTO) {
        log.info("Creating hotel with name: {}", hotelRequestDTO.getName());
        Hotel hotel = modelMapper.map(hotelRequestDTO, Hotel.class);
        hotel.setActive(false); // Default to inactive until approved

        hotel.setOwner(getCurrentUser()); // Set the owner of the hotel to the currently authenticated user

        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Hotel created with ID: {}", savedHotel.getId());
        return modelMapper.map(savedHotel, HotelResponseDto.class);
    }

    @Override
    public List<HotelResponseDto> getAllHotels() {
        User currentUser = getCurrentUser();
        log.info("Fetching all hotels for user: {}", currentUser.getUsername());
        List<Hotel> hotels = hotelRepository.findByOwner(currentUser);
        return hotels.stream()
                .map(hotel -> modelMapper.map(hotel, HotelResponseDto.class))
                .toList();
    }

    @Override
    public HotelResponseDto getHotelById(Long id) {
        log.info("Fetching hotel with ID: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + id));

        User currentUser = getCurrentUser();
        if (!hotel.getOwner().equals(currentUser)) {
            throw new AccessDeniedException("You are not authorized to view this hotel");
        }

        return modelMapper.map(hotel, HotelResponseDto.class);
    }

    @Override
    @Transactional
    public HotelResponseDto updateHotel(Long id, HotelUpdateRequestDto hotelUpdateRequestDTO) {
        log.info("Updating hotel with ID: {}", id);
        Hotel existingHotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + id));

        // Only the owner of the hotel can update it
        User currentUser = getCurrentUser();
        if (!existingHotel.getOwner().equals(currentUser)) {
            throw new AccessDeniedException("You are not authorized to update this hotel");
        }

        // Track if city changed (needs inventory sync)
        String oldCity = existingHotel.getCity();
        boolean cityChanged = false;

        if (hotelUpdateRequestDTO.getName() != null) {
            existingHotel.setName(hotelUpdateRequestDTO.getName());
        }
        if (hotelUpdateRequestDTO.getCity() != null) {
            existingHotel.setCity(hotelUpdateRequestDTO.getCity());
            cityChanged = !hotelUpdateRequestDTO.getCity().equals(oldCity);
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

        // Sync inventory if city changed
        if (cityChanged) {
            inventoryService.updateHotelInventoryCity(updatedHotel.getId(), hotelUpdateRequestDTO.getCity());
        }

        log.info("Hotel updated with ID: {}", updatedHotel.getId());
        return modelMapper.map(updatedHotel, HotelResponseDto.class);
    }

    @Override
    @Transactional
    public void deleteHotel(Long id) {
        log.info("Deleting hotel with ID: {}", id);
        Hotel existingHotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + id));

        // Only the owner of the hotel can delete it
        User currentUser = getCurrentUser();
        if (!existingHotel.getOwner().equals(currentUser)) {
            throw new AccessDeniedException("You are not authorized to delete this hotel");
        }

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
        for (var room : existingHotel.getRooms()) {
            inventoryService.initializeRoomForAYear(room);
        }

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


    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getHotelBookings(Long hotelId) {
        User currentUser = getCurrentUser();
        log.info("Fetching bookings for hotel ID: {} by user: {}", hotelId, currentUser.getUsername());

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));

        if (!hotel.getOwner().equals(currentUser)) {
            throw new AccessDeniedException("You are not authorized to view bookings for this hotel");
        }

        List<Booking> bookings = bookingRepository.findByHotel(hotel);

        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingResponseDto.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HotelReportResponseDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating report for hotel ID: {} from {} to {}", hotelId, startDate, endDate);

        // Default to last 30 days
        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now(); // Default to today

        // Ensure that the hotel exists and belongs to the current user
        User currentUser = getCurrentUser();
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));
        if (!hotel.getOwner().equals(currentUser)) {
            throw new AccessDeniedException("You are not authorized to view the report for this hotel");
        }

        // Fetch all bookings for the hotel within the specified date range
        List<Booking> bookings = bookingRepository.findByHotelAndCheckInDateBetween(hotel, startDate, endDate);

        // Calculate total confirmed bookings, total revenue from confirmed bookings, and average revenue per booking
        long totalConfirmedBookings = bookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
                .count();

        BigDecimal totalRevenueOfConfirmedBookings = bookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
                .map(Booking::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageRevenuePerBooking = totalConfirmedBookings > 0
                ? totalRevenueOfConfirmedBookings.divide(BigDecimal.valueOf(totalConfirmedBookings), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return HotelReportResponseDto.builder()
                .totalBookings(totalConfirmedBookings)
                .totalRevenue(totalRevenueOfConfirmedBookings)
                .averageRevenuePerBooking(averageRevenuePerBooking)
                .build();
    }
}
