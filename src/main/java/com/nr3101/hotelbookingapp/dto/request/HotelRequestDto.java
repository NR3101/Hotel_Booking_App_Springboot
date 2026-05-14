package com.nr3101.hotelbookingapp.dto.request;

import com.nr3101.hotelbookingapp.entity.HotelContactInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request payload to create a new hotel")
public class HotelRequestDto {

    @NotBlank(message = "Hotel name is required")
    @Schema(description = "Hotel name", example = "Grand Palace Hotel")
    private String name;

    @Schema(description = "City where the hotel is located", example = "Mumbai")
    private String city;

    @Schema(description = "Array of photo URLs")
    private String[] photos;

    @Schema(description = "Array of amenity names", example = "[\"WiFi\", \"Pool\", \"Gym\"]")
    private String[] amenities;

    @Schema(description = "Hotel contact information")
    private HotelContactInfo contactInfo;
}
