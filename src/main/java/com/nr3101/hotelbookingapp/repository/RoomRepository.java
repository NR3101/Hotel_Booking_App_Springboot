package com.nr3101.hotelbookingapp.repository;

import com.nr3101.hotelbookingapp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

	Optional<Room> findByIdAndHotelId(Long id, Long hotelId);
}
