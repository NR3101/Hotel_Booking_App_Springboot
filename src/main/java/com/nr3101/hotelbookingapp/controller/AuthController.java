package com.nr3101.hotelbookingapp.controller;

import com.nr3101.hotelbookingapp.dto.request.LoginRequestDto;
import com.nr3101.hotelbookingapp.dto.request.SignupRequestDto;
import com.nr3101.hotelbookingapp.dto.response.LoginResponseDto;
import com.nr3101.hotelbookingapp.dto.response.UserResponseDto;
import com.nr3101.hotelbookingapp.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Signup, login, and token refresh endpoints")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user", description = "Creates a new user account and returns user details")
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signUp(@Valid @RequestBody SignupRequestDto request) {
        return new ResponseEntity<>(authService.signUp(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Login", description = "Authenticates the user and returns a JWT access token. A refresh token is set as an HTTP-only cookie.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto request,
            HttpServletResponse response
    ) {
        String[] tokens = authService.login(request);
        String accessToken = tokens[0];
        String refreshToken = tokens[1];

        // Set the refresh token as an HTTP-only cookie
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true); // Prevent access from JavaScript

        // Set cookie properties (e.g., secure, max age)
        cookie.setSecure(true); // Only send over HTTPS
        cookie.setPath("/"); // Cookie is valid for the entire application
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days

        response.addCookie(cookie);

        return ResponseEntity.ok(new LoginResponseDto(accessToken));
    }

    @Operation(summary = "Refresh access token", description = "Uses the refresh token cookie to issue a new JWT access token")
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(HttpServletRequest request) {
        // Extract the refresh token from the cookie
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("Refresh token not found"));

        String newAccessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new LoginResponseDto(newAccessToken));
    }
}
