package com.nr3101.hotelbookingapp.dto.request;

import com.nr3101.hotelbookingapp.entity.HotelContactInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request payload to update an existing hotel")
public class HotelUpdateRequestDto {

    @Schema(description = "Updated hotel name")
    private String name;

    @Schema(description = "Updated city")
    private String city;

    @Schema(description = "Updated photo URLs")
    private String[] photos;

    @Schema(description = "Updated amenities")
    private String[] amenities;

    @Schema(description = "Updated contact information")
    private HotelContactInfo contactInfo;

    @Schema(description = "Whether the hotel is active")
    private Boolean active;
}
