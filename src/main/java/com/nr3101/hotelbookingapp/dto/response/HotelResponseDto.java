package com.nr3101.hotelbookingapp.dto.response;

import com.nr3101.hotelbookingapp.entity.HotelContactInfo;
import lombok.Data;

@Data
public class HotelResponseDto {

    private Long id;
    private String name;
    private String city;
    private String[] photos;
    private String[] amenities;
    private HotelContactInfo contactInfo;
    private Boolean active;
}
