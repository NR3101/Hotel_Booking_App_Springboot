package com.nr3101.hotelbookingapp.dto.request;

import com.nr3101.hotelbookingapp.entity.role.Gender;
import lombok.Data;

@Data
public class GuestRequestDto {

    private String name;
    private Gender gender;
    private Integer age;
}

