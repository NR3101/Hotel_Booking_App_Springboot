package com.nr3101.hotelbookingapp.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HotelSearchRequestDto {

    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer roomsCount;

    private Integer page = 0;
    private Integer size = 10;
}
