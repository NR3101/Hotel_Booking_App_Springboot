package com.nr3101.hotelbookingapp.dto.response;

import com.nr3101.hotelbookingapp.entity.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelPriceResponseDto {

    private Hotel hotel;
    private Double price;

}
