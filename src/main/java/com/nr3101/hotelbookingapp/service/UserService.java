package com.nr3101.hotelbookingapp.service;

import com.nr3101.hotelbookingapp.dto.request.ProfileUpdateRequestDto;
import com.nr3101.hotelbookingapp.dto.response.UserResponseDto;
import com.nr3101.hotelbookingapp.entity.User;

public interface UserService {

    User getUserById(Long id);


    void updateUserProfile(ProfileUpdateRequestDto profileUpdateRequest);

    UserResponseDto getUserProfile();
}
