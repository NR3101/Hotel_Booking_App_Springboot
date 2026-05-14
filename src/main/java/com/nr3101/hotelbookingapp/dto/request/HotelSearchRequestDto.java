package com.nr3101.hotelbookingapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Search criteria for finding available hotels")
public class HotelSearchRequestDto {

    @Schema(description = "City to search in", example = "Mumbai")
    private String city;

    @Schema(description = "Check-in date", example = "2026-06-01")
    private LocalDate startDate;

    @Schema(description = "Check-out date", example = "2026-06-05")
    private LocalDate endDate;

    @Schema(description = "Number of rooms needed", example = "1")
    private Integer roomsCount;

    @Schema(description = "Page number (0-indexed)", example = "0")
    private Integer page = 0;

    @Schema(description = "Page size", example = "10")
    private Integer size = 10;
}
