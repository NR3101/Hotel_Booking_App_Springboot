package com.nr3101.hotelbookingapp.repository;

import com.nr3101.hotelbookingapp.entity.Booking;
import com.nr3101.hotelbookingapp.entity.Hotel;
import com.nr3101.hotelbookingapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPaymentSessionId(String sessionId);

    List<Booking> findByHotel(Hotel hotel);

    List<Booking> findByUser(User user);

    List<Booking> findByHotelAndCheckInDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);
}