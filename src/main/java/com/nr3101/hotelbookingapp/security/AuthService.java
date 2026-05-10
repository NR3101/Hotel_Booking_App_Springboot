package com.nr3101.hotelbookingapp.security;

import com.nr3101.hotelbookingapp.advice.EmailAlreadyExistsException;
import com.nr3101.hotelbookingapp.dto.request.LoginRequestDto;
import com.nr3101.hotelbookingapp.dto.request.SignupRequestDto;
import com.nr3101.hotelbookingapp.dto.response.LoginResponseDto;
import com.nr3101.hotelbookingapp.dto.response.UserResponseDto;
import com.nr3101.hotelbookingapp.entity.User;
import com.nr3101.hotelbookingapp.entity.enums.Role;
import com.nr3101.hotelbookingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserResponseDto signUp(SignupRequestDto request) {
        log.info("Attempting to sign up user with email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already in use");
        }

        // Create new user
        User newUser = modelMapper.map(request, User.class);
        newUser.setPassword(passwordEncoder.encode(request.getPassword())); // Hash the password
        newUser.setRoles(Set.of(Role.GUEST)); // Default role

        // Save user to database
        User savedUser = userRepository.save(newUser);
        log.info("User signed up successfully with email: {}", savedUser.getEmail());
        return modelMapper.map(savedUser, UserResponseDto.class);
    }

    public String[] login(LoginRequestDto request) {
        log.info("Attempting to log in user with email: {}", request.getEmail());

        // Authenticate the user (this will throw an exception if authentication fails)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        // If authentication is successful, generate JWT tokens
        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("User logged in successfully with email: {}", user.getEmail());
        return new String[]{accessToken, refreshToken};
    }

    public String refreshToken(String refreshToken) {
        log.info("Attempting to refresh access token using refresh token");

        // Validate the refresh token and extract user ID
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Generate a new access token
        String newAccessToken = jwtService.generateAccessToken(user);
        log.info("Access token refreshed successfully for user ID: {}", userId);
        return newAccessToken;
    }
}
