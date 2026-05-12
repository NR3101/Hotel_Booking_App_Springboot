package com.nr3101.hotelbookingapp.repository;

import com.nr3101.hotelbookingapp.entity.Hotel;
import com.nr3101.hotelbookingapp.entity.Inventory;
import com.nr3101.hotelbookingapp.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * "@Lock" annotation is used to prevent concurrent modifications to the same inventory records during the booking process
 * PESSIMISTIC_WRITE lock will block other transactions from reading or writing the locked records until the current transaction is completed.
 * "@Modifying" annotation is used to indicate that the query is an update operation, which allows Spring Data JPA to execute it properly.
 */

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    void deleteByRoom(Room room);

    // Find hotels with enough free inventory for the requested stay.
    @Query("""
                SELECT DISTINCT i.hotel
                FROM Inventory i
                    WHERE i.city = :city
                      AND i.date BETWEEN :startDate AND :endDate
                      AND i.closed = false
                      AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
                GROUP BY i.hotel, i.room
                HAVING COUNT(i.date) = :dateCount
            """)
    Page<Hotel> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );

    // Lock available rows before reserving them.
    @Query("""
                SELECT i
                FROM Inventory i
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND i.closed = false
                  AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableInventoriesForBooking(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    // Lock reserved rows before converting them to booked.
    @Query("""
                SELECT i
                FROM Inventory i
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND (i.totalCount - i.bookedCount) >= :roomsCount
                  AND i.closed = false
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockReservedInventoriesForBooking(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    // Move rooms from free to reserved while payment is pending.
    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.reservedCount = i.reservedCount + :roomsCount
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
                  AND i.closed = false
            """)
    int initBooking(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );


    // Move rooms from reserved to booked after payment succeeds.
    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.reservedCount = i.reservedCount - :roomsCount,
                    i.bookedCount = i.bookedCount + :roomsCount
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND (i.totalCount - i.bookedCount) >= :roomsCount
                  AND i.reservedCount >= :roomsCount
                  AND i.closed = false
            """)
    int confirmBooking(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    // Move rooms back from booked to available during cancellation.
    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.bookedCount = i.bookedCount - :roomsCount
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND (i.totalCount - i.bookedCount) >= :roomsCount
                  AND i.closed = false
            """)
    int cancelBooking(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    List<Inventory> findByHotelAndDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);
}
