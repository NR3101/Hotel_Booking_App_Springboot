package com.nr3101.hotelbookingapp.repository;

import com.nr3101.hotelbookingapp.entity.Hotel;
import com.nr3101.hotelbookingapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByOwner(User user);
}
