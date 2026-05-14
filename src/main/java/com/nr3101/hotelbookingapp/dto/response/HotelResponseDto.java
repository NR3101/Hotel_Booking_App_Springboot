package com.nr3101.hotelbookingapp.dto.response;

import com.nr3101.hotelbookingapp.entity.HotelContactInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Hotel summary information")
public class HotelResponseDto {

    @Schema(description = "Hotel ID")
    private Long id;

    @Schema(description = "Hotel name")
    private String name;

    @Schema(description = "City")
    private String city;

    @Schema(description = "Photo URLs")
    private String[] photos;

    @Schema(description = "Amenities")
    private String[] amenities;

    @Schema(description = "Contact information")
    private HotelContactInfo contactInfo;

    @Schema(description = "Whether the hotel is active")
    private Boolean active;
}
