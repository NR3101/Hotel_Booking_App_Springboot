package com.nr3101.hotelbookingapp.repository;

import com.nr3101.hotelbookingapp.dto.response.HotelPriceResponseDto;
import com.nr3101.hotelbookingapp.entity.Hotel;
import com.nr3101.hotelbookingapp.entity.HotelMinPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface HotelMinPriceRepository extends JpaRepository<HotelMinPrice, Long> {

    @Query("""
                SELECT NEW com.nr3101.hotelbookingapp.dto.response.HotelPriceResponseDto(h.hotel, AVG(h.minPrice))
                FROM HotelMinPrice h
                    WHERE h.hotel.city = :city
                      AND h.date BETWEEN :startDate AND :endDate
                      AND h.hotel.active = true
                GROUP BY h.hotel
            """)
    Page<HotelPriceResponseDto> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    Optional<HotelMinPrice> findByHotelAndDate(Hotel hotel, LocalDate date);
}