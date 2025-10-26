package com.example.demo.service;

import com.example.demo.model.Reservation;
import com.example.demo.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // NEW METHOD: Get total income per hotel
    public Map<String, Double> getHotelRevenue() {
        List<Object[]> revenueData = reservationRepository.findRevenueByHotel();
        Map<String, Double> revenueMap = new HashMap<>();

        for (Object[] row : revenueData) {
            String hotelName = (String) row[0];
            Double totalRevenue = (Double) row[1];
            // Ensure revenue is not null before putting into map
            if (totalRevenue != null) {
                revenueMap.put(hotelName, totalRevenue);
            }
        }
        return revenueMap;
    }

    public Set<Long> getBookedRoomIds(LocalDate checkInDate, LocalDate checkOutDate) {
        return reservationRepository.findAll().stream()
                .filter(r ->
                        r.getCheckInDate().isBefore(checkOutDate) &&
                                r.getCheckOutDate().isAfter(checkInDate)
                )
                .map(Reservation::getRoomId)
                .collect(Collectors.toSet());
    }

    public void checkRoomAvailability(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Long reservationId) {

        List<Reservation> overlaps = reservationRepository.findOverlappingReservations(
                roomId, checkInDate, checkOutDate
        );

        if (reservationId != null) {
            overlaps.removeIf(r -> r.getId().equals(reservationId));
        }

        if (!overlaps.isEmpty()) {
            throw new RuntimeException("Room is already reserved for the period: " + checkInDate + " to " + checkOutDate + ".");
        }
    }

    @Transactional
    public Reservation saveReservation(Reservation reservation) {
        if (reservation.getRoomId() == null || reservation.getCheckInDate() == null || reservation.getCheckOutDate() == null) {
            throw new IllegalArgumentException("Reservation data is incomplete.");
        }

        checkRoomAvailability(
                reservation.getRoomId(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                null
        );

        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation updateReservationDates(Long id, LocalDate newCheckInDate, LocalDate newCheckOutDate) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);

        if (optionalReservation.isEmpty()) {
            throw new RuntimeException("Reservation not found.");
        }

        Reservation existingReservation = optionalReservation.get();
        Long roomId = existingReservation.getRoomId();
        double pricePerNight = existingReservation.getPrice();

        checkRoomAvailability(roomId, newCheckInDate, newCheckOutDate, id);

        long nights = ChronoUnit.DAYS.between(newCheckInDate, newCheckOutDate);
        if (nights <= 0) {
            throw new IllegalArgumentException("Check-out date must be after Check-in date.");
        }
        double newTotalPrice = pricePerNight * nights;

        existingReservation.setCheckInDate(newCheckInDate);
        existingReservation.setCheckOutDate(newCheckOutDate);
        existingReservation.setTotalPrice(newTotalPrice);

        return reservationRepository.save(existingReservation);
    }

    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }
}