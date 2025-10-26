package com.example.demo.repository;

import com.example.demo.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query("SELECT h FROM Hotel h LEFT JOIN FETCH h.rooms r LEFT JOIN FETCH h.images i")
    List<Hotel> findAllWithRoomsAndImages();

    @Query("SELECT h FROM Hotel h LEFT JOIN FETCH h.rooms r LEFT JOIN FETCH h.images i WHERE h.id = :id")
    Optional<Hotel> findByIdWithRoomsAndImages(Long id);
}