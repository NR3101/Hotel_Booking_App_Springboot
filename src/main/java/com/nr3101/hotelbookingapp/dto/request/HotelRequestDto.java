package com.nr3101.hotelbookingapp.dto.request;

import com.nr3101.hotelbookingapp.entity.HotelContactInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HotelRequestDto {

    @NotBlank(message = "Hotel name is required")
    private String name;

    private String city;

    private String[] photos;
    private String[] amenities;
    private HotelContactInfo contactInfo;
}
