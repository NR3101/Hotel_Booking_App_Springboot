package com.nr3101.hotelbookingapp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Hotel booking and revenue report")
public class HotelReportResponseDto {

    @Schema(description = "Total number of bookings")
    private Long totalBookings;

    @Schema(description = "Total revenue earned")
    private BigDecimal totalRevenue;

    @Schema(description = "Average revenue per booking")
    private BigDecimal averageRevenuePerBooking;
}
