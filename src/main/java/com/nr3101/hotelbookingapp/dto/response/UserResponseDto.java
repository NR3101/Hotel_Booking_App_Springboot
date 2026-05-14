package com.nr3101.hotelbookingapp.dto.response;

import com.nr3101.hotelbookingapp.entity.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "User profile information")
public class UserResponseDto {

    @Schema(description = "User ID")
    private Long id;

    @Schema(description = "Email address")
    private String email;

    @Schema(description = "Display name")
    private String name;

    @Schema(description = "Phone number")
    private String phoneNumber;

    @Schema(description = "Date of birth")
    private LocalDate dateOfBirth;

    @Schema(description = "Gender")
    private Gender gender;
}
