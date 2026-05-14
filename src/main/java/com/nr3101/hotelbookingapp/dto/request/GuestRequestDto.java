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
@Schema(description = "Guest details")
public class GuestRequestDto {

    @Size(min = 1, max = 100, message = "Name must be between 1 and 50 characters")
    @Schema(description = "Guest name", example = "Jane Doe")
    private String name;

    @Schema(description = "Guest gender")
    private Gender gender;

    @Past(message = "Date of birth must be in the past")
    @Schema(description = "Date of birth", example = "1995-08-15")
    private LocalDate dateOfBirth;
}
