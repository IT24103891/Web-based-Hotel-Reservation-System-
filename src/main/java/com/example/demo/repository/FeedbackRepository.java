package com.example.demo.repository;

import com.example.demo.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByHotelId(Long hotelId);

    // ⭐ Fix/Update: Method to calculate the average rating for a given hotel
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.hotel.id = :hotelId")
    Double findAverageRatingByHotelId(Long hotelId);
}