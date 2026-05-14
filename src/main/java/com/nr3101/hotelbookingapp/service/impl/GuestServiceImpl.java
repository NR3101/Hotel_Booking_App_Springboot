package com.nr3101.hotelbookingapp.service.impl;

import com.nr3101.hotelbookingapp.dto.request.GuestRequestDto;
import com.nr3101.hotelbookingapp.dto.response.GuestResponseDto;
import com.nr3101.hotelbookingapp.entity.Guest;
import com.nr3101.hotelbookingapp.entity.User;
import com.nr3101.hotelbookingapp.repository.GuestRepository;
import com.nr3101.hotelbookingapp.service.GuestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.nr3101.hotelbookingapp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<GuestResponseDto> getAllGuests() {
        log.info("Fetching all guests for the current user");

        // Get the current user
        User currentUser = getCurrentUser();

        List<Guest> guests = guestRepository.findByUser(currentUser);

        return guests.stream()
                .map(guest -> modelMapper.map(guest, GuestResponseDto.class))
                .toList();
    }

    @Override
    public GuestResponseDto addNewGuest(GuestRequestDto guestRequestDto) {
        // Get the current user
        User currentUser = getCurrentUser();

        log.info("Adding new guest for user with id: {}", currentUser.getId());

        Guest guest = modelMapper.map(guestRequestDto, Guest.class);
        guest.setUser(currentUser);

        Guest savedGuest = guestRepository.save(guest);
        log.info("Guest added with ID: {}", savedGuest.getId());
        return modelMapper.map(savedGuest, GuestResponseDto.class);
    }

    @Override
    public void updateGuest(Long guestId, GuestRequestDto guestRequestDto) {
        log.info("Updating guest with ID: {}", guestId);

        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new RuntimeException("Guest not found"));

        User currentUser = getCurrentUser();
        if (!currentUser.equals(guest.getUser())) {
            throw new AccessDeniedException("You are not the owner of this guest");
        }

        modelMapper.map(guestRequestDto, guest);
        guest.setUser(currentUser);
        guest.setId(guestId);

        guestRepository.save(guest);

        log.info("Guest with ID: {} updated successfully", guestId);
    }

    @Override
    public void deleteGuest(Long guestId) {
        log.info("Deleting guest with ID: {}", guestId);

        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new RuntimeException("Guest not found"));

        User currentUser = getCurrentUser();
        if (!currentUser.equals(guest.getUser())) {
            throw new AccessDeniedException("You are not the owner of this guest");
        }

        guestRepository.deleteById(guestId);
    }
}
