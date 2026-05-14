package com.nr3101.hotelbookingapp.dto.response;

import com.nr3101.hotelbookingapp.entity.Hotel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Hotel search result with computed price")
public class HotelPriceResponseDto {

    @Schema(description = "Hotel entity")
    private Hotel hotel;

    @Schema(description = "Computed price for the search criteria")
    private Double price;
}
