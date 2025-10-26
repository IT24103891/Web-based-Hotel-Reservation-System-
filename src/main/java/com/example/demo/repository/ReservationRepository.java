package com.example.demo.repository;

import com.example.demo.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.roomId = :roomId " +
            "AND r.checkInDate < :checkOutDate " +
            "AND r.checkOutDate > :checkInDate")
    List<Reservation> findOverlappingReservations(Long roomId, LocalDate checkInDate, LocalDate checkOutDate);

    List<Reservation> findByUserId(Long userId);

    // NEW Query: Calculate total revenue per hotel
    // Returns a List of Object arrays, where each array is [hotelName, totalRevenue]
    @Query("SELECT r.hotelName, SUM(r.totalPrice) FROM Reservation r GROUP BY r.hotelName")
    List<Object[]> findRevenueByHotel();
}