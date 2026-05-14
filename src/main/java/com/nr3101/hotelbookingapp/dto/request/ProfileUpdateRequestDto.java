package com.nr3101.hotelbookingapp.dto.request;

import com.nr3101.hotelbookingapp.entity.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Profile update fields (all optional)")
public class ProfileUpdateRequestDto {

    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Schema(description = "Display name", example = "John Doe")
    private String name;

    @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits")
    @Schema(description = "10-digit phone number", example = "9876543210")
    private String phoneNumber;

    @Past(message = "Date of birth must be in the past")
    @Schema(description = "Date of birth", example = "1990-01-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Gender")
    private Gender gender;
}
