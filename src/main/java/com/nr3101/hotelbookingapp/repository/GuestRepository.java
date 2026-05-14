package com.nr3101.hotelbookingapp.repository;

import com.nr3101.hotelbookingapp.entity.Guest;
import com.nr3101.hotelbookingapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    List<Guest> findByUser(User currentUser);
}