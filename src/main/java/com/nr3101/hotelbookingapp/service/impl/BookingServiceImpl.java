package com.nr3101.hotelbookingapp.service.impl;

import com.nr3101.hotelbookingapp.advice.ResourceNotFoundException;
import com.nr3101.hotelbookingapp.dto.request.BookingRequestDto;
import com.nr3101.hotelbookingapp.dto.request.GuestRequestDto;
import com.nr3101.hotelbookingapp.dto.response.BookingResponseDto;
import com.nr3101.hotelbookingapp.entity.*;
import com.nr3101.hotelbookingapp.entity.enums.BookingStatus;
import com.nr3101.hotelbookingapp.repository.*;
import com.nr3101.hotelbookingapp.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final GuestRepository guestRepository;

    private final ModelMapper modelMapper;
    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;


    @Override
    @Transactional
    public BookingResponseDto initializeBooking(BookingRequestDto bookingRequest) {
        log.info("Initializing booking for hotelId: {}, roomId: {}",
                bookingRequest.getHotelId(), bookingRequest.getRoomId());

        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + bookingRequest.getHotelId()));

        Room room = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + bookingRequest.getRoomId()));

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventoriesForBooking(
                room.getId(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(),
                bookingRequest.getRoomsCount()
        );

        long daysCount = ChronoUnit.DAYS
                .between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()) + 1;

        if (inventoryList.size() != daysCount) {
            throw new IllegalStateException("Not enough inventory available for the selected dates");
        }

        // Reserve the room by updating the reserved count in the inventory
        inventoryList.forEach(inventory -> {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
            inventoryRepository.save(inventory);
        });

        // Create and save booking
        Booking booking = Booking.builder()
                .hotel(hotel)
                .room(room)
                .roomsCount(bookingRequest.getRoomsCount())
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .status(BookingStatus.RESERVED)
                .user(getCurrentUser()) // Set the actual user later
                .amount(BigDecimal.TEN) // Placeholder for amount calculation logic
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        return modelMapper.map(savedBooking, BookingResponseDto.class);
    }

    @Override
    @Transactional
    public BookingResponseDto addGuestsToBooking(Long bookingId, List<GuestRequestDto> guestRequests) {
        log.info("Adding guests to booking with id: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has expired. Please initialize a new booking.");
        }

        if (booking.getStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Guests can only be added to a booking in RESERVED status.");
        }

        guestRequests.forEach(guestRequest -> {
            Guest guest = modelMapper.map(guestRequest, Guest.class);
            guest.setUser(getCurrentUser()); // Set the actual user later
            Guest savedGuest = guestRepository.save(guest);
            booking.getGuests().add(savedGuest);
        });


        booking.setStatus(BookingStatus.GUESTS_ADDED);
        Booking updatedBooking = bookingRepository.save(booking);
        return modelMapper.map(updatedBooking, BookingResponseDto.class);
    }

    public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    // TODO: Remove dummy user and set the actual user when the booking is confirmed
    public User getCurrentUser() {
        User user = new User();
        user.setId(1L);
        return user;
    }
}
