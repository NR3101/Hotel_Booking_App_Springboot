package com.nr3101.hotelbookingapp.service;

import com.nr3101.hotelbookingapp.entity.Room;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteFutureInventories(Room room);
}
