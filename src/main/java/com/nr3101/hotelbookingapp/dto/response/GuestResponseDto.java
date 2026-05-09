package com.nr3101.hotelbookingapp.dto.response;

import com.nr3101.hotelbookingapp.entity.User;
import com.nr3101.hotelbookingapp.entity.enums.Gender;
import lombok.Data;

@Data
public class GuestResponseDto {

    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
}

