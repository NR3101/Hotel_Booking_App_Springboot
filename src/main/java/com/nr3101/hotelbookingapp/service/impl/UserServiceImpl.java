package com.nr3101.hotelbookingapp.service.impl;

import com.nr3101.hotelbookingapp.advice.ResourceNotFoundException;
import com.nr3101.hotelbookingapp.dto.request.ProfileUpdateRequestDto;
import com.nr3101.hotelbookingapp.dto.response.UserResponseDto;
import com.nr3101.hotelbookingapp.entity.User;
import com.nr3101.hotelbookingapp.repository.UserRepository;
import com.nr3101.hotelbookingapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.nr3101.hotelbookingapp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public UserResponseDto getUserProfile() {
        log.info("Fetching user profile for the current user");

        // Get the currently authenticated user
        User currentUser = getCurrentUser();

        // Map the User entity to UserResponseDto
        UserResponseDto userProfile = modelMapper.map(currentUser, UserResponseDto.class);

        log.info("User profile fetched successfully for user id: {}", currentUser.getId());
        return userProfile;
    }

    @Override
    public void updateUserProfile(ProfileUpdateRequestDto profileUpdateRequest) {
        log.info("Updating user profile with data: {}", profileUpdateRequest);

        // Get the currently authenticated user
        User currentUser = getCurrentUser();

        // Update the user's profile with the new data
        if (profileUpdateRequest.getName() != null) {
            currentUser.setName(profileUpdateRequest.getName());
        }
        if (profileUpdateRequest.getPhoneNumber() != null) {
            currentUser.setPhoneNumber(profileUpdateRequest.getPhoneNumber());
        }
        if (profileUpdateRequest.getDateOfBirth() != null) {
            currentUser.setDateOfBirth(profileUpdateRequest.getDateOfBirth());
        }
        if (profileUpdateRequest.getGender() != null) {
            currentUser.setGender(profileUpdateRequest.getGender());
        }

        // Save the updated user back to the database
        userRepository.save(currentUser);

        log.info("User profile updated successfully for user id: {}", currentUser.getId());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by email: {}", username);
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }
}
