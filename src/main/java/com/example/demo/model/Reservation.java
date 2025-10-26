package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate; // NEW IMPORT

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NEW FIELDS
    private Long userId;        // Link to the user who made the reservation
    private Long roomId;        // Link to the actual room
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    // Existing room details (for easy display/decoupling)
    private String hotelName;
    private String roomNumber;
    private String type;
    private double price;

    // NEW: Field for total price (price * nights)
    private double totalPrice;
}