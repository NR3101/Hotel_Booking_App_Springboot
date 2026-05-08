package com.nr3101.hotelbookingapp.repository;

import com.nr3101.hotelbookingapp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}