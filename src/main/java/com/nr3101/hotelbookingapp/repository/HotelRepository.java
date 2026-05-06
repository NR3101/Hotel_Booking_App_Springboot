package com.nr3101.hotelbookingapp.repository;

import com.nr3101.hotelbookingapp.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
