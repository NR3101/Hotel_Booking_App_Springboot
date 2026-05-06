package com.nr3101.hotelbookingapp.repository;

import com.nr3101.hotelbookingapp.entity.Inventory;
import com.nr3101.hotelbookingapp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    void deleteByRoomAndDateAfter(Room room, LocalDate today);
}
