package com.nr3101.hotelbookingapp.dto.request;

import com.nr3101.hotelbookingapp.entity.HotelContactInfo;
import lombok.Data;

@Data
public class HotelUpdateRequestDto {

    private String name;
    private String city;
    private String[] photos;
    private String[] amenities;
    private HotelContactInfo contactInfo;
    private Boolean active;
}

