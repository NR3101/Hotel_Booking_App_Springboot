package com.nr3101.hotelbookingapp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Daily room inventory record")
public class InventoryResponseDto {

    @Schema(description = "Inventory record ID")
    private Long id;

    @Schema(description = "Date for this inventory record")
    private LocalDate date;

    @Schema(description = "Number of rooms booked")
    private Integer bookedCount;

    @Schema(description = "Number of rooms temporarily reserved")
    private Integer reservedCount;

    @Schema(description = "Total rooms available")
    private Integer totalCount;

    @Schema(description = "Dynamic pricing surge factor")
    private BigDecimal surgeFactor;

    @Schema(description = "Computed price for this date")
    private BigDecimal price;

    @Schema(description = "Whether the room is closed for this date")
    private Boolean closed;

    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
