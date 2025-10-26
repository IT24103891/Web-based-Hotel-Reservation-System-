package com.example.demo.repository;

import com.example.demo.model.HotelService;
import com.example.demo.model.Hotel; // Import Hotel model
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HotelServiceRepository extends JpaRepository<HotelService, Long> {
    // NEW: JPA automatically implements this method to find services linked to a specific hotel
    List<HotelService> findByHotel(Hotel hotel);
}