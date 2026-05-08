package com.nr3101.hotelbookingapp.repository;

import com.nr3101.hotelbookingapp.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}