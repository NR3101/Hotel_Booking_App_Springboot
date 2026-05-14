package com.nr3101.hotelbookingapp.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@Schema(description = "Hotel contact and location details")
public class HotelContactInfo {

    @Schema(description = "Street address", example = "123 Marine Drive")
    private String address;

    @Schema(description = "Contact phone number", example = "022-12345678")
    private String phoneNumber;

    @Schema(description = "Contact email", example = "info@grandpalace.com")
    private String email;

    @Schema(description = "Geo-location as 'latitude,longitude'", example = "19.0760,72.8777")
    private String location; // e.g., "latitude,longitude"
}
