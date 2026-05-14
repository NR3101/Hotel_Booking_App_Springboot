package com.nr3101.hotelbookingapp.dto.response;

import com.nr3101.hotelbookingapp.entity.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Guest information")
public class GuestResponseDto {

    @Schema(description = "Guest ID")
    private Long id;

    @Schema(description = "Guest name")
    private String name;

    @Schema(description = "Gender")
    private Gender gender;

    @Schema(description = "Date of birth")
    private LocalDate dateOfBirth;
}
