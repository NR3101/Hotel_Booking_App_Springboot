package com.nr3101.hotelbookingapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request payload to update room inventory for a date range")
public class UpdateInventoryRequestDto {

    @Schema(description = "Start date of the update range", example = "2026-06-01")
    private LocalDate startDate;

    @Schema(description = "End date of the update range", example = "2026-06-15")
    private LocalDate endDate;

    @Schema(description = "Surge factor to apply to the base price", example = "1.5")
    private BigDecimal surgeFactor;

    @Schema(description = "Whether to close the room for the date range")
    private Boolean closed;
}
