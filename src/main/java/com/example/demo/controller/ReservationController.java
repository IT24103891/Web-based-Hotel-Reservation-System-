package com.example.demo.controller;

import com.example.demo.model.Reservation;
import com.example.demo.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:5500")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        try {
            Reservation savedReservation = service.saveReservation(reservation);
            return ResponseEntity.ok(savedReservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(null);
        }
    }

    @PutMapping("/{id}/dates")
    @PreAuthorize("hasRole('ADMIN') or @reservationSecurity.isReservationOwner(#id)")
    public ResponseEntity<Reservation> updateReservationDates(@PathVariable Long id, @RequestBody java.util.Map<String, String> dates) {
        try {
            LocalDate checkIn = LocalDate.parse(dates.get("checkInDate"));
            LocalDate checkOut = LocalDate.parse(dates.get("checkOutDate"));

            Reservation updatedReservation = service.updateReservationDates(id, checkIn, checkOut);
            return ResponseEntity.ok(updatedReservation);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(null);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(null);
            }
            return ResponseEntity.status(409).body(null);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Reservation> getAllReservations() {
        return service.getAllReservations();
    }

    // NEW ENDPOINT: Get total revenue grouped by hotel
    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')") // Only Admins should see financial data
    public Map<String, Double> getHotelRevenue() {
        return service.getHotelRevenue();
    }

    @GetMapping("/user/{userId}")
    public List<Reservation> getReservationsByUserId(@PathVariable Long userId) {
        return service.getReservationsByUserId(userId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @reservationSecurity.isReservationOwner(#id)")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        service.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}